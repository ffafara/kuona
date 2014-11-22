kuona
=====

kuona is a Delivery Dashboard Generator. Input data is collected from a number of sources (initially Jenkins CI server).
Once the data is collected in some kind of store or database it is processed for viewing and graphical visualisation.

Getting Started
---------------

Kuona is released as a single _fat_ jar https://github.com/kuona/kuona/releases/ containing everything you need to build a dashboard. After downloading the jar you can check that everything is ok

	java -jar kuona-complete.jar

```bash
kuona version 0.0.1
Usage:
kuona <command> [command-args]

kuona run in a project folder without any parameters updates the project data by reading from the configured CI systems.

Commands:

create name    Create a new projects in the named directory. Once created you can update the config.yml
               file with the required CI settings.

```

	java -jar kuona-complete.jar create project-name

Will create a new folder _project-name_ containing a config.yml file and a subdirectory _site containing the html, javascript, css and everything else for the dashboard.

Running

	java -jar kuona-complete.jar serve

in the project directory runs the web server on port 8080 so point your browser at http://localhost:8080

There won't be much there but it confirms that the site has been created and is able to serve basic content.

## Update config.yml

This file controls how Kuona behaves.

You can define build servers (Jenkins) and version control systems (Subversion) that are used to source data.

Running

	java -jar kuona-complete.jar update

Pulls data from those servers and generates aggregated data for the dashboard.

You can run multiple copies of Kuona at the same time, typically the server and updater.

Updates to config.yml are only done on startup so if you make changes to the site section you will need to restart the server.
