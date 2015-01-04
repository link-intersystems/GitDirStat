GitDirStat
=============

GitDirStat is a GIT maintenance application written in java and based on the [JGIT](https://eclipse.org/jgit/) library provided by [eclipse.org](https://eclipse.org).

Features
-------

* List file paths in a git repository and the total size that they allocate in the repository's history.
* Remove paths from the history. Like [`git filter-branch --index-filter`](http://git-scm.com/docs/git-filter-branch).
* Table and tree view for path selection.


-----------
Usage
-----
You need Java 1.6. or higher to execute GitDirStat. Open a command line and execute `java -version` to see which version you have installed.

Download the [latest release](https://github.com/link-intersystems/GitDirStat/releases/latest).

If your `open with`([for win7](http://windows.microsoft.com/en-us/windows/change-file-open-program#1TC=windows-7)) is configured to execute `.jar` files using the `java`runtime you can just
double-click on the `GitDirStat-<VERSION>-gui.jar`.

If you want to use the command line type

    java -jar GitDirStat-<VERSION>-gui.jar [GIT_REPOSITORY_PATH]

to execute GitDirStat.

The parameter `GIT_REPOSITORY_PATH` is optional and can be omitted. If you provide this parameter the UI will start and open this repository. You can
open a repository in the UI using `File -> Open Git Repository`.

Screenshots
-----
### Analyzing repository
![GitDirStat Screenshot](src/site/res/GitDirStat_AnalyseRepository.PNG?raw=true)

### Table result
![GitDirStat Screenshot](src/site/res/GitDirStat_Tableview.PNG?raw=true)

### Tree result
![GitDirStat Screenshot](src/site/res/GitDirStat_Treeview.PNG?raw=true)

### Remove paths from repository
![GitDirStat Screenshot](src/site/res/GitDirStat_RemovePaths.PNG?raw=true)


