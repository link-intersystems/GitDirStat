GitDirStat
=============

GitDirStat is a GIT maintenance application written in java and based on the [JGIT](https://eclipse.org/jgit/) library provided by [eclipse.org](https://eclipse.org).

Features
-------

* List file paths in a git repository and the total size that they allocate in the repository's history.
* Remove paths from the history. Like [`git filter-branch --index-filter`](http://git-scm.com/docs/git-filter-branch).
* Table and tree view.


-----------
Usage
-----
You need a Java 1.6. or higher to execute GitDirStat. Open a command line and execute `java -version` to see which version you have installed.

Download the [latest release](https://github.com/link-intersystems/GitDirStat/releases/latest), open a command line and execute GitDirStat using:

    java -jar GitDirStat-<VERSION>-gui.jar [GIT_REPOSITORY_PATH]

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


