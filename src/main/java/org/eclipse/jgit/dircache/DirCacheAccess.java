package org.eclipse.jgit.dircache;

import static org.eclipse.jgit.lib.FileMode.TREE;
import static org.eclipse.jgit.lib.TreeFormatter.entrySize;

import java.io.IOException;
import java.lang.reflect.Field;

import org.eclipse.jgit.errors.UnmergedPathException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TreeFormatter;

public class DirCacheAccess {

	private Repository repo;
	private DirCache dirCache;

	public DirCacheAccess(Repository repo, DirCache dirCache) {
		this.repo = repo;
		this.dirCache = dirCache;
	}

	public ObjectId computeId() throws IOException {
		ObjectInserter odi = repo.newObjectInserter();
		ObjectId id;
		int cIdx = 0;
		int pathOffset = 0;
		DirCacheTree cacheTree = dirCache.getCacheTree(true);
		int entrySpan = cacheTree.getEntrySpan();
		final int endIdx = cIdx + entrySpan;
		final TreeFormatter fmt = new TreeFormatter(computeSize(cacheTree,
				toEntries(), cIdx, pathOffset, odi));
		int childIdx = 0;
		int entryIdx = cIdx;

		while (entryIdx < endIdx) {
			final DirCacheEntry e = dirCache.getEntry(entryIdx);
			final byte[] ep = e.path;
			if (childIdx < cacheTree.getChildCount()) {
				final DirCacheTree st = cacheTree.getChild(childIdx);
				if (st.contains(ep, pathOffset, ep.length)) {
					fmt.append(getEncodedName(st), TREE, st.getObjectId());
					entryIdx += st.getEntrySpan();
					childIdx++;
					continue;
				}
			}

			fmt.append(ep, pathOffset, ep.length - pathOffset, e.getFileMode(),
					e.idBuffer(), e.idOffset());
			entryIdx++;
		}

		id = odi.insert(fmt);
		return id;

	}

	private DirCacheEntry[] toEntries() {
		DirCacheEntry[] dirCacheEntries = new DirCacheEntry[dirCache
				.getEntryCount()];

		for (int i = 0; i < dirCacheEntries.length; i++) {
			dirCacheEntries[i] = dirCache.getEntry(i);
		}

		return dirCacheEntries;
	}

	private byte[] getEncodedName(DirCacheTree dirCacheTree) {
		try {
			Field encodedNameField = DirCacheTree.class.getField("encodedName");
			encodedNameField.setAccessible(true);
			return (byte[]) encodedNameField.get(dirCacheTree);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private int computeSize(DirCacheTree dirCacheTree,
			final DirCacheEntry[] cache, int cIdx, final int pathOffset,
			final ObjectInserter ow) throws UnmergedPathException, IOException {
		final int endIdx = cIdx + dirCacheTree.getEntrySpan();
		int childIdx = 0;
		int entryIdx = cIdx;
		int size = 0;

		while (entryIdx < endIdx) {
			final DirCacheEntry e = cache[entryIdx];
			if (e.getStage() != 0)
				throw new UnmergedPathException(e);

			final byte[] ep = e.getRawPath();
			int childCount = dirCacheTree.getChildCount();
			if (childIdx < childCount) {
				final DirCacheTree st = dirCacheTree.getChild(childIdx);
				if (st.contains(ep, pathOffset, ep.length)) {
					final int stOffset = pathOffset + st.nameLength() + 1;
					st.writeTree(cache, entryIdx, stOffset, ow);

					size += entrySize(TREE, st.nameLength());

					entryIdx += st.getEntrySpan();
					childIdx++;
					continue;
				}
			}

			size += entrySize(e.getFileMode(), ep.length - pathOffset);
			entryIdx++;
		}

		return size;
	}

	public ObjectId getTreeObjectId() {
		DirCacheTree cacheTree = dirCache.getCacheTree(true);
		return cacheTree.getObjectId();
	}

	public DirCache getDirCache() {
		return dirCache;
	}
}
