GitDirStat
=============

GitDirStat is a GIT maintenance application written in java and based on the [JGIT](https://eclipse.org/jgit/) library provided by [eclipse.org](https://eclipse.org).

Features
-------

* List file paths in a git repository and the total size that they allocate in the repository's history.
* Remove paths from the history. Like [`git filter-branch --index-filter`](http://git-scm.com/docs/git-filter-branch).
* Table and tree view.


-----------


![GitDirStat Screenshot](src/site/res/GitDirStat_RemovePaths.PNG?raw=true)


Usage
-----

    java -jar GitDirStat-0.0.1-SNAPSHOT-gui.jar
