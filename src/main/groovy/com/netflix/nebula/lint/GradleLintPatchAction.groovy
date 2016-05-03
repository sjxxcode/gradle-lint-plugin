import static FileMode.Symlink
        if (patchFixes.size() == 1 && patchFixes.get(0) instanceof GradleLintDeleteFile)
        else if (patchFixes.size() == 1 && patchFixes.get(0) instanceof GradleLintCreateFile) {
    static readFileOrSymlink(File file, FileMode mode) {
        return mode == Symlink ? [readSymbolicLink(file.toPath()).toString()] : file.readLines()
    static diffHints(String relativePath, PatchType patchType, FileMode fileMode) {
        def headers = ["diff --git a/$relativePath b/$relativePath"]
                headers += "new file mode ${fileMode.mode}"
                headers += "deleted file mode ${fileMode.mode}"
        for(patchSet in patchSets) {
            patchSet.eachWithIndex{ fix, i ->
                if(i < patchSet.size() - 1) {
                    def next = patchSet[i+1]
                    def multipleInsertionsAtSameLine = fix.from() > fix.to() && next.from() > next.to()

                    if ((fix.from() <= next.from() && fix.to() >= next.to() ||
                            next.from() <= fix.from() && next.to() >= fix.to()) &&
                            !multipleInsertionsAtSameLine) {
                        next.markAsUnfixed(UnfixedViolationReason.OverlappingPatch)
                    }
                }
            }
            patchSet.retainAll { it.reasonForNotFixing == null }
        }

            def fileMode = patchType == Create ? (patchFixes[0] as GradleLintCreateFile).fileMode : FileMode.fromFile(file)
                    readFileOrSymlink(file, fileMode).size() == 0) : true
            def newlineAtEndOfOriginal = emptyFile ? false : fileMode != Symlink && file.text[-1] == '\n'
            if (!emptyFile) lines += readFileOrSymlink(file, fileMode)
            def relativePath = project.rootDir.toPath().relativize(file.toPath()).toString()
                ${diffHints(relativePath, patchType, fileMode)}
                |--- ${patchType == Create ? '/dev/null' : 'a/' + relativePath}
                |+++ ${patchType == Delete ? '/dev/null' : 'b/' + relativePath}
}