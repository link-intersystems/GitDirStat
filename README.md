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

    java -jar GitDirStat-1.0.0-gui.jar [GIT_REPOSITORY_PATH]

Screenshots
-----
### Analysing repository
![GitDirStat Screenshot](src/site/res/GitDirStat_AnalyseRepository.PNG?raw=true)

### Table result
![GitDirStat Screenshot](src/site/res/GitDirStat_Tableview.PNG?raw=true)

### Tree result
![GitDirStat Screenshot](src/site/res/GitDirStat_Treeview.PNG?raw=true)

### Remove paths from repository
![GitDirStat Screenshot](src/site/res/GitDirStat_RemovePaths.PNG?raw=true)


