param(
    [string] $BasePath = "<PATH_TO_FOLDER>",
    [string] $JarPath = "target\PlannerTests-2.0.jar"
)

# (1) Define sets of parameters
$l_values = 1..3
$r_values = @(10, 15, 20, 25, 30)
$a_values = @(50, 100, 150, 200)
$v_values = @(10, 15, 20, 25, 30)

# (2) Log of processed combinations
$logFile = Join-Path $BasePath "process_log.txt"
if (!(Test-Path $logFile)) {
    New-Item -ItemType File -Path $logFile | Out-Null
}

# (3) Helper function: check if combination is logged
function AlreadyProcessed($l, $r, $a, $v) {
    $entry = "$l,$r,$a,$v"
    if (Get-Content $logFile | Select-String -Pattern "^$entry$") {
        return $true
    }
    return $false
}

# (4) Docker planners to run
$planners = @(
    @{
        name    = "seq-sat-lama-2011"
        command = 'aibasel/downward --plan-file /pddl/plan-seq-sat-lama-2011.sas --alias seq-sat-lama-2011 /pddl/domain.pddl /pddl/problem.pddl'
        logfile = "log-seq-sat-lama-2011.txt"
    },
    @{
        name    = "astar-blind"
        command = 'aibasel/downward /pddl/domain.pddl /pddl/problem.pddl --search "astar(blind())"'
        logfile = "log-astar-blind.txt"
    },
    @{
        name    = "symba2"
        command = 'ansep/symba2-64bit'
        logfile = "log-symba2.txt"
    },
    @{
        name    = "complementary2"
        command = 'ansep/complementary2'
        logfile = "log-complementary2.txt"
    },
    @{
        name    = "ragnarok"
        command = 'ansep/ragnarok'
        logfile = "log-ragnarok.txt"
    }
)

# (5) A helper function to run Docker with a timeout and detect errors
function Invoke-DockerWithTimeout {
    param(
        [string] $FolderName, # e.g. "1_10_50_10_new"
        [string] $FolderPath, # e.g. "<PATH_TO_FOLDER>\1_10_50_10_new"
        [string] $DockerCommand, # e.g. 'aibasel/downward --plan-file /pddl/plan.sas ...'
        [string] $PlannerName, # e.g. "downward-lama"
        [int]    $TimeoutSeconds    # e.g. 300
    )

    # Generate a unique container name
    $containerName = "planner_${FolderName}_$PlannerName"

    # Build the full 'docker run' command with container name
    $fullCommand = 'docker run --name ' + $containerName + ' --rm -v "' + $FolderPath + ':/pddl" ' + $DockerCommand

    Write-Host "Running Docker: $fullCommand"

    # Start a background job that runs cmd.exe /c "docker run ..."
    $jobScript = {
        param($cmd)
        & cmd /c $cmd 2>&1
    }
    $job = Start-Job -ScriptBlock $jobScript -ArgumentList $fullCommand

    $finished = $false
    $outputLines = @()
    $errorDetected = $false
    $errorMessage = ""

    try {
        # Wait up to the given timeout
        $jobDone = Wait-Job $job -Timeout $TimeoutSeconds
        if ($jobDone) {
            # Completed in time
            $finished = $true
            $outputLines = Receive-Job $job

            # Check for known Docker error patterns in the output
            foreach ($line in $outputLines) {
                if ($line -match 'Internal Server Error') {
                    $errorDetected = $true
                    $errorMessage += "$line`n"
                }
                elseif ($line -match 'Error') {
                    $errorDetected = $true
                    $errorMessage += "$line`n"
                }
                # Add more patterns as needed
            }
        }
        else {
            # Timed out => kill the container
            Write-Warning "Container $containerName timed out after $TimeoutSeconds seconds, killing..."
            # Attempt docker kill
            & docker kill $containerName | Out-Null

            # Stop the background job
            Stop-Job $job
            $errorDetected = $true
            $errorMessage += "Docker command timed out after $TimeoutSeconds seconds.`n"
        }
    }
    finally {
        # Cleanup job
        Remove-Job $job -Force | Out-Null
    }

    return [PSCustomObject]@{
        Finished      = $finished
        Lines         = $outputLines
        ContainerName = $containerName
        ErrorDetected = $errorDetected
        ErrorMessage  = $errorMessage.Trim()
    }
}

# (6) A helper to read or create `generation_info.json` in a folder
function Get-GenerationInfoObject($folderPath) {
    $jsonPath = Join-Path $folderPath "generation_info.json"
    if (Test-Path $jsonPath) {
        $jsonContent = Get-Content $jsonPath -Raw | ConvertFrom-Json
        $hash = [ordered]@{}
        foreach ($property in $jsonContent.PSObject.Properties) {
            $hash[$property.Name] = $property.Value
        }
        return $hash
    }
    else {
        return [ordered]@{}
    }
}

function Save-GenerationInfoObject($folderPath, $obj) {
    $jsonPath = Join-Path $folderPath "generation_info.json"
    $obj | ConvertTo-Json -Depth 10 | Out-File $jsonPath -Encoding UTF8
}

# (7) The main loops with error handling
try {
    foreach ($l in $l_values) {
        foreach ($r in $r_values) {
            foreach ($a in $a_values) {
                foreach ($v in $v_values) {
                    if (AlreadyProcessed $l $r $a $v) {
                        Write-Host "Already processed: l=$l r=$r a=$a v=$v. Skip."
                        continue
                    }

                    # Reuse museum (now is generating new museums)
                    $reuseCmd = "java -jar `"$JarPath`" -o `"$BasePath`" -r $r -a $a -l $l -v $v"
                    Write-Host "CMD: $reuseCmd"
                    & cmd /c $reuseCmd

                    # Now folders: "X_new", "X_old", "X_parametric"
                    $comboName = "${l}_${r}_${a}_${v}"
                    $folderSuffixes = @("_new", "_old", "_parametric")
                    foreach ($sfx in $folderSuffixes) {
                        $folderName = $comboName + $sfx
                        $folderPath = Join-Path $BasePath $folderName

                        if (!(Test-Path $folderPath)) {
                            Write-Host "Folder $folderPath not found; skipping"
                            continue
                        }

                        # For each Docker planner
                        foreach ($pl in $planners) {
                            $cName = $pl.name
                            $cCommand = $pl.command
                            $logF = $pl.logfile

                            $logPath = Join-Path $folderPath $logF

                            # Run Docker and handle potential errors
                            $result = Invoke-DockerWithTimeout -FolderName $folderName -FolderPath $folderPath -DockerCommand $cCommand -PlannerName $cName -TimeoutSeconds 300
                            $lines = $result.Lines

                            if ($result.ErrorDetected) {
                                Write-Error "[$cName / $folderName] Docker Error Detected: $($result.ErrorMessage)"
                                # Log the error to a separate file
                                $errorLogPath = Join-Path $folderPath "docker_error_$cName.log"
                                $result.ErrorMessage | Out-File -FilePath $errorLogPath -Encoding UTF8
                                # Stop the entire script
                                Write-Host "Critical Docker error encountered. Stopping the script."
                                exit 1
                            }

                            if ($result.Finished) {
                                $finished = $true
                            }
                            else {
                                $finished = $false
                            }

                            # Write lines to log
                            $lines | Out-File $logPath -Encoding UTF8

                            # Initialize variables to capture time and solution status
                            $foundSolution = $false
                            $timeVal = -1

                            if ($cName -eq "astar-blind") {
                                $planLines = @()
                            }
                            
                            foreach ($line in $lines) {
                                # Capture Total time
                                if ($line -cmatch 'Total time:\s+(\d+(\.\d+)?)s') {
                                    $timeVal = [double]$Matches[1]
                                    Write-Host "[$cName / $folderName] Total time: $timeVal s"
                                }

                                # Capture Solution status
                                if ($line -match '(?i)no solution found|No solution found|completely explored state space -- no solution|search stopped without finding a solution|Search stopped without finding a solution') {
                                    $foundSolution = $false
                                    Write-Host "[$cName / $folderName] No solution found."
                                }
                                elseif ($line -cmatch 'Solution found\.') {
                                    $foundSolution = $true
                                    Write-Host "[$cName / $folderName] Solution found."
                                }

                                # If collecting plan, look for lines starting with 'visit-' or 'move-'
                                if ($cName -eq "astar-blind") {
                                    if ($line -match '^\s*(visit-|move-)') {
                                        $trimmedLine = $line.Trim()
                                        $planLines += $trimmedLine
                                    }
                                    elseif ($line -match '^\[t=\d+\.\d+s, \d+ KB\]\s+Plan (length|cost):\s+\d+') {
                                        $trimmedLine = $line.Trim()
                                        $planLines += $trimmedLine
                                    }
                                }
                            }

                            # After processing all lines, check if plan was collected
                            if ($cName -eq "astar-blind" -and $foundSolution -and $planLines.Count -gt 0) {
                                # Define the plan file path
                                $planFileName = "plan-$cName.sas"
                                $planFilePath = Join-Path $folderPath $planFileName
                            
                                # Write the plan lines to the plan file
                                $planLines | Out-File -FilePath $planFilePath -Encoding UTF8
                            
                                Write-Host "[$cName / $folderName] Plan extracted and saved to $planFileName"
                            }
                            elseif ($cName -eq "astar-blind" -and $foundSolution -and $planLines.Count -eq 0) {
                                Write-Warning "[$cName / $folderName] Solution found but no plan steps were extracted."
                            } 
                            elseif ($cName -ne "astar-blind" -and $foundSolution) {
                                #wait to make sure the planner has finished writing the plan
                                Start-Sleep -Seconds 1
                                #Move plan.sas to plan-planner name.sas
                                if (Test-Path "$folderPath\plan.sas") {
                                    Move-Item "$folderPath\plan.sas" "$folderPath\plan-$cName.sas"
                                }
                            }
                            

                            # Update or create generation_info.json
                            $genObj = Get-GenerationInfoObject $folderPath
                            # We'll store the time in "time-planner-{cName}" and "solution_found-{cName}"
                            $fieldTime = "time-planner-$cName"
                            $fieldSolution = "solution_found-$cName"

                            if ($finished) {
                                # Assign time
                                if ($timeVal -ne -1) {
                                    $genObj["$fieldTime"] = $timeVal
                                }
                                else {
                                    $genObj["$fieldTime"] = -1
                                }

                                # Assign solution_found
                                $genObj["$fieldSolution"] = $foundSolution
                            }
                            else {
                                # Timed out => set time = 99999 and solution_found = false
                                $genObj["$fieldTime"] = 99999
                                $genObj["$fieldSolution"] = $false
                            }

                            Save-GenerationInfoObject $folderPath $genObj

                        } # end foreach planner
                    } # end folderSuffix

                    # Mark this combination done
                    Add-Content $logFile "$l,$r,$a,$v"
                } # end $v
            }
        }
    }

    Write-Host "All done."
}
catch {
    Write-Error "An unexpected error occurred: $_"
    exit 1
}
