/* 
Jenkinsfile demo with sample configuration values, in the form of Declarative Pipeline syntax
Created by: Jackie CAI
Version: 1.0
Date: May-24-2019
*/

/* 
Prerequisites:
a) Jenkins version: any only if support plugin.
b) Pipeline plugin version: 2.5 and above.

How to make full use of this reference file, two ways:
1) modify all the required parts manually (if you know some syntax).
comment out or remove any unnecessary sections/directives for your configuration or change the sample values to your needs.

2) use "Snippet Generator" to generate piece of code for steps, and 
   use "Declarative Directive Generator" to generate code for all directives.
make sure the prerequisites are fulfilled, create a new item selecting "Pipeline" style, click "Pipeline Syntax" at the side
bar of the item, then, you will all the tools mentioned here. as well in here, you can find all the reference documents matching your 
Jenkins and plugin versions, such as "Steps Reference" and "Global Variables Reference".

*/

/* Online documents: (may not match your Jenkins version and plugin versions) 
   for more detail information of the syntax, refer to: https://jenkins.io/doc/book/pipeline/syntax/
   for all the list of steps, refer to:	https://jenkins.io/doc/pipeline/steps/
*/

/*  Directives summary (Y:yes, N:no, n:several times. ">=1")
USAGE LEVEL:      pipeline      stage
environment       Y*1             Y*1
options           Y*1             partially 
parameters        Y*1             N
triggers          Y*1             N
stage             Y*n             N
tools             Y*1             Y*1
input             N               Y*1
when              N               Y*1

Sections/blocks - contain one or more Directives or Steps:
0) pipeline: only once
1) agent: several times
2) post: once
3) stages: once at the top leve, at most once for each nested stage
4) steps: at most once for each stage
5) parallel: at most once for each stage 
*/

/* top-level block */
pipeline { 
    /* agent section - required, can be in pipeline or any stage, and each only at most once.
	[parameters]
	any - Execute the Pipeline, or stage, on any available agent
	none - When applied at the top-level of the pipeline block no global agent will be allocated for the entire Pipeline run and
	       each stage section will need to contain its own agent section
	label - Execute the Pipeline, or stage, on an agent available in the Jenkins environment with the provided label.
	node - agent { node { label 'labelName' } } behaves the same as agent { label 'labelName' }, but node allows for additional options (such as customWorkspace).
	docker - Execute the Pipeline, or stage, with the given container which will be dynamically provisioned on a node pre-configured to 
	         accept Docker-based Pipelines, or on a node matching the optionally defined label parameter
			agent {
					docker {
						image 'maven:3-alpine'
						label 'my-defined-label'
						args  '-v /tmp:/tmp'
						registryUrl 'https://myregistry.com/'
						registryCredentialsId 'myPredefinedCredentialsInJenkins'
					}
		}
    dockerfile - Execute the Pipeline, or stage, with a container built from a Dockerfile contained in the source repository. In order to use this option, 
	             the Jenkinsfile must be loaded from either a Multibranch Pipeline, or a "Pipeline from SCM."
			agent {
					// Equivalent to "docker build -f Dockerfile.build --build-arg version=1.0.2 ./build/
					dockerfile {
						filename 'Dockerfile.build'
						dir 'build'
						label 'my-defined-label'
						additionalBuildArgs  '--build-arg version=1.0.2'
						args '-v /tmp:/tmp'
					} 
			}
	[Common Otions:] 
	label - [string] for parameter node(required), docker, dockerfile
	customWorkspace - [string] for node, docker, dockerfile
	reuseNode - [boolean] for docker, dockerfile
	args - [string] for docker, dockerfile
	*/
	agent any

	/*
	   environment directive - optional, inside pipeline block or inside stage directives.
	                a sequence of key-value pairs for all steps or stage-specific steps.
	                support a special helper function credentials(). 
	   Supported credentials type:
	   Secret text - the environment variable specified will be set to the Secret Text content.
	   Secret file - the environment variable specified will be set to the location of the File file that is temporarily created.
	   Username and password - the environment variable specified will be set to username:password and two additional environment
	                 variables will be automatically defined: MYVARNAME_USR and MYVARNAME_PSW respectively.
	   SSH with Private Key - the environment variable specified will be set to the location of the SSH key file that is temporarily 
              created and two additional environment variables may be automatically defined: MYVARNAME_USR and MYVARNAME_PSW (holding the passphrase).

	*/
	environment {
		/* Two variables will be generated: SERVICE_CREDS_USR, SERVICE_CREDS_PSW  */
		//SERVICE_CREDS = credentials('my-prefined-username-password')

		/* relative path of the ci scripts in the project workspace */
		CI_SCRIPT_RELATIVE_PATH = './ci/'
	}

   /* options directive - optional. only once, inside pipeline block, or inside stage block.
	          allows configuring Pipeline-specific options from within the Pipeline itself.
			  pipeline provides a number of options, some plugins also provide options.

	below list all available options with sample values.
	[stage level applicable] - this option can also be used inside a stage block.
    Inside a stage, the steps in the options directive are invoked before entering the agent or checking any when conditions.
   */
   options {
		/* Persist artifacts and console output for the specific number of recent Pipeline runs */
        buildDiscarder(logRotator(numToKeepStr: '5'))

		/* Perform the automatic source control checkout in a subdirectory of the workspace.*/
		//checkoutToSubdirectory('repo1')

		/* Disallow concurrent executions of the Pipeline. Can be useful for preventing simultaneous accesses to shared resources, etc.  */
		disableConcurrentBuilds()

		/*  Do not allow the pipeline to resume if the master restarts. */
		disableResume()

		/*  Used with docker or dockerfile top-level agent. When specified, each stage will run in a new container instance on the same node,
		    rather than all stages running in the same container instance.*/
        newContainerPerStage()

		/*  Allows overriding default treatment of branch indexing triggers.*/
		overrideIndexTriggers(true) 

		/*  Preserve stashes from completed builds, for use with stage restarting. */
		preserveStashes(buildCount: 5)

		/*  Set the quiet period, in seconds, for the Pipeline, overriding the global default. */
		quietPeriod(30)

		/*  On failure, retry the entire Pipeline the specified number of times. [stage level applicable]**/
		retry(2)

		/*  Skip checking out code from source control by default in the agent directive. [stage level applicable]*/
		//skipDefaultCheckout() 

		/*  Skip stages once the build status has gone to UNSTABLE. */
		skipStagesAfterUnstable()
		
		/*  Set a timeout period for the Pipeline run, after which Jenkins should abort the Pipeline. [stage level applicable]**/
		timeout(time: 2, unit: 'HOURS') 

		/*  Prepend all console output generated by the Pipeline run with the time at which the line was emitted. [stage level applicable]**/
		//timestamps()

		/*  Set failfast true for all subsequent parallel stages in the pipeline.*/
		parallelsAlwaysFailFast()

		/* various plugin options */
   }

   /* parameters directive - optional, only once, inside pipeline block.
                provides a list of parameters which a user should provide when triggering the Pipeline.
				The values for these user-specified parameters are made available to Pipeline steps via the params object.
				to reference the parameter, use the format(double quote): ${params.name}
                eg: echo "Hello ${params.PERSON}"
   */
   //parameters {
		/*  A parameter of a string type */
		//string(name: 'DEPLOY_ENV', defaultValue: 'staging', description: '')

		/*  A text parameter, which can contain multiple lines */
		//text(name: 'DEPLOY_TEXT', defaultValue: 'One\nTwo\nThree\n', description: '')
		
		/*  A boolean parameter */
		//booleanParam(name: 'DEBUG_BUILD', defaultValue: true, description: '')
		
		/*  A choice parameter */
		//choice(name: 'CHOICES', choices: ['one', 'two', 'three'], description: '')
		
		/*  A file parameter, which specifies a file to be submitted by the user when scheduling a build */
		//file(name: 'FILE', description: 'Some file to upload')

		/*  A password parameter */
		//password(name: 'PASSWORD', defaultValue: 'SECRET', description: 'A secret password')

   //}

   /* triggers directive - optional, only once, inside pipeline block. 
           The triggers directive defines the automated ways in which the Pipeline should be re-triggered. For Pipelines which are integrated with a source such as
           GitHub or BitBucket, triggers may not be necessary as webhooks-based integration will likely already be present
   */
   triggers {
		/* Accepts a cron-style string to define a regular interval at which the Pipeline should be re-triggered.
		                   MINUTE       HOUR       DOM                MONTH       DOW
         cron like format: Minute(0-59) Hour(0-24) Day_of_month(1-31) Month(1-12) Day_of_week(0-7)
		 "0" and "7" are Sunday for day_of_week.
		 special abbreviations: @daily, @weekly @annualy
		 *: all valid values
		 M-N: a range of values
		 M-N/X or any/X: steps by intervals of X through the specified range or whole valid range
		 A,B,...,Z: enumerates multiple values
		 H symbol: can be thought of as a random value over a range, but it actually is a hash of the job name, 
		           not a random function, so that the value remains stable for any given project
		*/
		cron('H 20 * * 1-5')  // 20:00 every working day

		/* Accepts a cron-style string to define a regular interval at which Jenkins should check for new source changes. 
		If new changes exist, the Pipeline will be re-triggered.available for Jenkins 2.22 or later */
		pollSCM('H(0-30)/10 */4 * * 1-5') // check SCM source changes every 4 hours in working day

		/* Accepts a comma separated string of jobs and a threshold. When any job in the string finishes with 
		the minimum threshold, the Pipeline will be re-triggered. */
		//upstream(upstreamProjects: 'job1,job2', threshold: hudson.model.Result.SUCCESS)
   }

    /* stages section  - required, only once
		Containing a sequence of one or more stage directives
        The stages section will typically follow the directives such as agent, options, etc.
	*/
	stages {
			/* stage directive - at least one inside top-level "stages" section, or many times in "parallel" or sub-"stages"
			         a stage MUST have one and only one of the following:
					 a) steps" - no further sub-stages
					 b) stages - for sequential stages
					             the nested "stages" can't contain any further "parallel" stages themselves.
					 c) parallel - for parallel stages
							 It is not possible to nest a "parallel" block within a stage directive if that stage directive
							 is nested within a parallel block itself. However, a stage directive within a parallel block can
							 use all other functionality of a stage, including agent, tools, when, etc.
			*/
			stage('Build') {
				//agent { /* optional */
				//		label "master build server"
				//}

				/* when directive - optional, inside a stage directive 
				        must contain at least one condition, if contain more conditions, all the child conditions must return
						true for the stage to execute.
						nested conditions: (any arbitary depth)
						a) not
						b) allOf
						c) anyOf
				[Build-in conditions] are listed below, you should choose only required conditions.
				*/
			    //when {
						/* By default, the when condition for a stage will be evaluated after entering the agent for that stage.
						   If beforeAgent is set to true, the when condition will be evaluated first, and the agent will only be
						   entered if the when condition evaluates to true */
						//beforeAgent true

						/* By default, the when condition for a stage will be evaluated before(after??) the input, 
						   If beforeInput is set to true, the when condition will be evaluated first, and the input will only be 
						   entered if the when condition evaluates to true.	*/
					    //beforeInput true 

 						/* Execute the stage when the branch being built matches the branch pattern given */ 
						//branch 'master'
						
						/* Execute the stage when the build is building a tag */
						//buildingTag()

						/* Execute the stage if the build’s SCM changelog contains a given regular expression pattern */
                        //changelog '.*^\\[DEPENDENCY\\] .+$'

						/* Execute the stage if the build’s SCM changeset contains one or more files matching the given string or glob. */
						//changeset "**/*.js"

						/* By default the path matching will be case insensitive, this can be turned off with the caseSensitive parameter */
						//changeset glob: "ReadMe.*", caseSensitive: true

						/* Executes the stage if the current build is for a "change request" (a.k.a. Pull Request on GitHub and Bitbucket, 
						   Merge Request on GitLab or Change in Gerrit etc.). */ 
						/* When no parameters are passed the stage runs on every change request */
						//changeRequest() 

						/* By adding a filter attribute with parameter to the change request, the stage can be made to run only on matching
						   change requests. Possible attributes are:
						   id, target, branch, fork, url, title, author, authorDisplayName, and authorEmail. 
						   Each of these corresponds to a CHANGE_* environment variable */
						//changeRequest target: 'master'

						/* The optional parameter comparator may be added after an attribute to specify how any patterns are evaluated for a match: 
						   EQUALS for a simple string comparison (the default), 
						   GLOB for an ANT style path glob (same as for example changeset), or 
						   REGEXP for regular expression matching */
						//changeRequest authorEmail: "[\\w_-.]+@example.com", comparator: 'REGEXP'

						/* Execute the stage when the specified environment variable is set to the given value */
						//environment name: 'DEPLOY_TO', value: 'production'

						/* Execute the stage when the expected value is equal to the actual value */
						//equals expected: 2, actual: currentBuild.number

						/* Execute the stage when the specified Groovy expression evaluates to true.
						  Note that when returning strings from your expressions they must be converted to booleans or return null to evaluate to false. 
						  Simply returning "0" or "false" will still evaluate to "true" */
						//expression { return params.DEBUG_BUILD }

						/* Execute the stage if the TAG_NAME variable matches the given pattern. */
						//tag "release-*"

						/* If an empty pattern is provided the stage will execute if the TAG_NAME variable exists (same as buildingTag()). */
						//tag()

						/* The optional parameter comparator may be added after an attribute to specify how any patterns are evaluated for a match: 
						   EQUALS for a simple string comparison, 
						   GLOB (the default) for an ANT style path glob (same as for example changeset), or 
						   REGEXP for regular expression matching.  */
						//tag pattern: "release-\\d+", comparator: "REGEXP"

						/* Execute the stage when the nested condition is false. Must contain one condition */
						//not { branch 'master' }

						/* Execute the stage when all of the nested conditions are true. Must contain at least one condition. */
						//allOf { branch 'master'; environment name: 'DEPLOY_TO', value: 'production' }

						/* Execute the stage when at least one of the nested conditions is true. Must contain at least one condition */
						//anyOf { branch 'master'; branch 'staging' }

						/* Execute the stage when the current build has been triggered by the param given */
						//triggeredBy cause: "UserIdCause", detail: "vlinde"
			    //}

				/* the input directive on a stage allows you to prompt for input, using the input step. the stage will pause after any options have been applied, 
				and before entering the stage`s `agent or evaluating its when condition. if the input is approved, the stage will then continue. any parameters 
				provided as part of the input submission will be available in the environment for the rest of the stage.
				*/
			    //input {
						/* Required. This will be presented to the user when they go to submit the input. */
						//message "Deploy to production?"

						/* An optional identifier for this input. Defaults to the stage name. */
						//id "simple-input"

						/* Optional text for the "ok" button on the input form. */
						//ok "Yes, we should."

						/* An optional comma-separated list of users or external group names who are allowed to submit this input. Defaults to allowing any user. */
						//submitter "alice,bob"

						/* An optional name of an environment variable to set with the submitter name, if present. */
						//submitterParameter

						/* An optional list of parameters to prompt the submitter to provide. See parameters for more information. */
						//parameters {
					    //		string(name: 'PERSON', defaultValue: 'Mr Jenkins', description: 'Who should I say hello to?')
						//}
                //}

				/* steps section - required for each stage
				defines a series of one or more steps to be executed in a given stage directive.
				the steps section must contain one or more steps.
				for all steps detail, refer to: https://jenkins.io/doc/pipeline/steps/
				*/
				steps {
						echo 'step1 of Build stage'
           				//checkout scm // use default checkout by skipDefaultCheckout() option

						/* to get the value of variable, you need use double quotes */
                        bat encoding: 'UTF-8', label: 'ci_build', returnStatus: true, script: "${env.CI_SCRIPT_RELATIVE_PATH}" + 'ci_build.bat'
				}
			}

			/*  Any stage containing "parallel" cannot contain agent or tools, since those are not relevant without steps.*/
			stage('Test') {
  				//when {
						//branch 'master'
				//}

				/* force parallel stages to all be aborted when one of them fails,
				   Another option for adding failfast is adding an option to the pipeline definition: parallelsAlwaysFailFast() */
				failFast true

				steps {
						echo 'step1 of Test'
                        bat encoding: 'UTF-8', label: 'ci_static_test', returnStatus: true, script: "${env.CI_SCRIPT_RELATIVE_PATH}" +  'ci_static_test.bat'
						
                        bat encoding: 'UTF-8', label: 'ci_unit_test', returnStatus: true, script: "${env.CI_SCRIPT_RELATIVE_PATH}" + 'ci_unit_test.bat'

                        bat encoding: 'UTF-8', label: 'ci_integration_test', returnStatus: true, script: "${env.CI_SCRIPT_RELATIVE_PATH}" + 'ci_integration_test.bat'
						
                        bat encoding: 'UTF-8', label: 'ci_validation_test', returnStatus: true, script: "${env.CI_SCRIPT_RELATIVE_PATH}" + 'ci_validation_test.bat'
				}

				/* the stages below within parallel block will be executed in parallel */
//				parallel {
//		                stage('Branch A') {
//							agent {
//								label "for-branch-a"
//							}							
//							steps {
//								echo "On Branch A"
//							}
//						}
//						stage('Branch B') {
//							agent {
//								label "for-branch-b"
//							}
//							steps {
//								echo "On Branch B"
//							}
//						}
//						stage('Branch C') {
//							agent {
//								label "for-branch-c"
//							}
//							/* there shall be NO further "parallel" stages inside the stages */
//							stages {
//								    stage('Nested 1') {
//											steps {
//												echo "In stage Nested 1 within Branch C"
//											}
//									}
//									stage('Nested 2') {
//											steps {
//												echo "In stage Nested 2 within Branch C"
//											}
//									}
//						   }
//				      }
//			    }
			}

			stage('Deploy') {
				steps {
						echo 'step1 of Deploy stage'
                        bat encoding: 'UTF-8', label: 'ci_sw_delivery', returnStatus: true, script: "${env.CI_SCRIPT_RELATIVE_PATH}" + 'ci_sw_delivery.bat'
				}
			}
	}

	/* post section - optional, in pipeline top-level or stage level, conventially at the end of pipeline
      - possible post-conditions
	  always - all pipeline or stage completion status
	  changed - current run status differs from previous run
	  fixed - current run is successful vs previous run failed or unstable
	  regression - current run is failure, unstable, or aborted vs previous run is successful
	  aborted - current run is aborted, usually mannually
	  failure - current run is failed
	  succes - current run is success
	  unstable - current run is unstable, usually caused by test failure, code violation etc.
	  unsuccessful - current run is not success status
	  cleanup - all other post condition has been evaluated, regardless of the current run status 

	*/
	post {
			always {
					echo 'always do post action'
					//archiveArtifacts artifacts: 'build/libs/**/*.jar', fingerprint: true
					//junit 'build/reports/**/*.xml'
			}
        success {
            echo 'I succeeeded!'
        }
        unstable {
            echo 'I am unstable :/'
        }
        failure {
            echo 'I failed :('
		 	//mail to: 'team@example.com',
            //     subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
            //     body: "Something is wrong with ${env.BUILD_URL}"
  		}
        changed {
            echo 'Things were different before...'
        }
	}

	/* tool section - optional, inside pipeline block or inside stage block
		    A section defining tools to auto-install and put on the PATH. This is ignored if agent none is specified.
		Supported Tools:  maven, jdk, gradle
		
	*/
    //tools {
	    /* The tool name must be pre-configured in Jenkins under Manage Jenkins → Global Tool Configuration.*/
        //maven 'apache-maven-3.0.1' 
    //}

}
