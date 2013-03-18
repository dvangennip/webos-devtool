This webOS developer tool helps to streamline the packaging, installing and running of webOS software. Essentially this tool provides a GUI for the HP webOS command-line tools on Apple's OS X. For some Integrated Development Environments such tools are available, for others support is less than perfect. This standalone tool can be used separate from any IDE as it deals directly with the project folder on disk.

It allows webOS developers to select the default device for any actions, plus it allows one-click packaging and installation. Logging of applications is also supported. A simple implementation of [JS Lint](http://www.jslint.com/) scanning is part of the application.

[![](http://dvangennip.github.com/webos-devtool/screenshot.png)](http://dvangennip.github.com/webos-devtool/screenshot.png) 

### Current status
This application was developed to target Moyo development, which required frequent packaging and uploading to a webOS emulator or device. Since the arrival of Enyo, most development can be done with the use of a browser. This means there is less need for this application, even though it is still functional. However, since the Summer of 2011 this project has not been in active development. Occasionally I fix a bug or add some support for Enyo as I see fit, but no major work has been done since.

#### Supported platforms
All webOS SDK versions are supported as far I have been able to determine.

Currently this tool is meant for use with Apple's Mac OS X and requires Java 6 to be installed. It has been tested on Snow Leopard. While it could be adjusted to work on Linux and Windows systems I have no way to test this. Basically command line commando's have to be adjusted as well as file paths, so adjustments should be trivial.

### Usage
Usage of the tool itself is quite straightforward. Open a project by selecting the project folder via the dialog. Select the desired project in the left source pane and use the options which become available on the right pane. Alternatively one can set the options and reach specific device settings via the source list. For all common actions shortcuts are available. See the menubar for those.

#### Requirements for use
Upon opening a project this tool checks whether the selected folder indeed contains a webOS software project. To enable successful detection a strict file structure is required. Within the main project folder there should be both a *app_src* folder in which all source files go, as well as a *bin* folder. Optionally *app_package* and *app_service* folders can be added to the root if such elements are required. The *bin* folder will hold packages ready for installation or further distribution. This structure is imposed to separate source and its resulting files ready for installation. The HP webOS packager tool simply grabs all files within the source folder, thus placing any unnecessary files within this place on disk will result in packages with inflated size. It is thus advised to use a separate folder for other files, documentation, and et cetera.

#### Project folder structure
* `app_src` - (application source files, including `appinfo.json`, `framework_config.json`, and `sources.json` or `depends.js`)
* `app_package` - (optional) (package source files, including `packageinfo.json`)
* `app_service` - (optional) (service framesource files, including `services.json`, `framework_config.json`, and `sources.json` or `depends.js`)
* `bin` - (created packages go here)
* ... - (any other folders related to the project)

#### Known quirks
* Tasks are handled one by one but tasks do not time out, so the application may occassionally get stuck at one task (especially when a device is not responding, such as a booting emulator). There's currently no way around this except restarting the application.
* The application does not save or remember anything.
* Using the deploy functionality for Enyo2 projects suffers from path and working directory problems. This is fixed by adding a few lines to the project's `<boilerplate-folder>/tools/deploy.sh` file:


```sh
	# set working directory to project root (avoids misconfigured path errors)
	PROJECT_ROOT="$TOOLS/.."
	cd $PROJECT_ROOT

	# make sure node is available in path (default location is /usr/local/bin/node)
	export PATH="/usr/local/bin:$PATH"

	# ADD THESE LINES BEFORE THE ONE THAT SAYS check for node, but quietly
```

### Installation
If you intend on developing and/or modifying this application see below for compiler instructions. If your primary interest is to use this application please have a look at my website where the [latest version](http://project.sinds1984.nl/) can be found.

### Compiling
The build process is handled by [ant](http://ant.apache.org/). I am not exactly sure whether this comes standard with OS X, otherwise check the ant website for installation instructions. Type *ant* at a terminal console to check (a message about build.xml not found means it works). Ant uses the build.xml file to go through all the necessary steps for compiling this tool. The following commands are supported:

* `ant` - default action which compiles the application into a self-executable jar file (for testing purposes)
* `ant app` - compiles the project and creates a nice webOSdevtool.app package (can be put in your Applications folder)
* `ant javadoc` - generates documentation in HTML files
* `ant clean` - cleans all existing built files and documentation

### Credits
This software relies on several external Java packages as well as default HP webOS tools for its main functionality and interface elements. As credit is due for the developers of those items, see the list below for the included packages and versions used. Please refer to these packages for specifics about their implementation and licenses. The *jar* files of these packages should be installed in the *lib* folder of this project and be correctly referenced in the manifest file (found in *mf* folder).

* [HP webOS command-line tools](https://developer.palm.com/content/api/dev-guide/tools/command-line-tools.html") - As part of the SDK
* [MacWidgets](http://code.google.com/p/macwidgets/) - Native looking Mac GUI widgets for Java
* [Google Gson](http://code.google.com/p/google-gson/) - JSON to Java converter package
* [jslint4java](http://code.google.com/p/jslint4java/) - Java wrapper around jslint

The following works have been included in this repository:

* [Blueprint icon](http://shlyapnikova.deviantart.com/gallery/#/d2ug0n4) - Adopted from [Anna Shlyapnikova](http://shlyapnikova.deviantart.com/)
* [System Executer](http://devdaily.com/java/java-processbuilder-process-system-exec) - By Alvin Alexander

### Some coding notes
This app was my first try at coding something a little more complex (development started in March 2011), so GUI and process handling is only partially disentangled. It is best seen as a lot of GUI code with a specific part meant for handling tasks (in a separate thread). Remember, code spaghetti is best eaten with a good temper :)

### License
Source code is available under a [Creative Commons Attribution-NonCommercial license](http://creativecommons.org/licenses/by-nc/3.0/), so you are free to do with it as you desire. I cannot support commercial use of this code as it depends on other's open source efforts for its functionality. It would be kind (but not necessary) to let me know.