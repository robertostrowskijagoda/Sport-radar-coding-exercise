This is recruitation project, please see the details in "Coding Exercise.pdf" file

About hack in tests (waitUntilNanoTimeChanges):
Sometimes tests run so fast, that two Matches have the same timestamp (such situation affects sorting).
I was planning to remove the timestamp and sort it by keys, but keys are long and sorting result must be int.
So to do that properly, we would have to limit keys range to int (and disable keys restoring), which I don't want to do - that is why I prefer that hack.

Actually after reconsidering it, we don't have to return full range in sorting, it can be limited to -1, 0, 1, so probably it would be possible, but max started matches would be then Long.MAX_VALUE, which is a lot, so probaly acceptable - let me know, if you wan't me to fix it, I can do it in about 15 minutes.
