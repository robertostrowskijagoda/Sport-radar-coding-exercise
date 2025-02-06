This is recruitation project, please see the details in "Coding Exercise.pdf" file

About hack in tests (waitUntilNanoTimeChanges):
Sometimes tests run so fast, that two Matches have the same timestamp (such situation affects sorting).
I was planning to remove the timestamp and sort it by keys, but keys are long and sorting result must be int.
So to do that properly, we would have to limit keys range to int, which I don't want to do - that is why I prefer that hack.
Sorry :)