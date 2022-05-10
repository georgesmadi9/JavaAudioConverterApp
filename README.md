# JavaAudioConverterApp

This project is a system that converts *wav* files to *mp3* files using [JAVE2](https://github.com/a-schild/jave2) (A Java wrapper on the ffmpeg project). This app can be used either in the background as a constant monitor to a directory or can be launched once and stopped it up to you to decide how you wish to use it.

## Features

- Converts wav files from a given directory to *mp3* files and stores them in another given directory. 
- Can be run in the background as it can constantly monitor changes to the source directory.
- If any conversion failed it will be restarted directly no need for any human intervention.
- Error logs are stored in the `logs.txt` file (modifiable).
- Every successful process is saved to the `processes.csv` file (modifiable).

## Technical Features

- Each conversion is done in a different thread than the other.
- Threads are ran by ascending order of creation date.
- Error logs have date and time associated with them to better look for any problem.
- Application can be used from the command line and all inputs and outputs can be modified.
- Successful processes are stored within the csv in the format:

| Feild | Type |
| ------ | ------ |
| Thread UID | String |
| File Name | String |

## Resources Used

These are the few libraries that were used and their purpose:

- **JAVE2** - Converter used for the conversion process.
- **WatcherService API** - Monitors changes to the source directory and notifies of any change.
- **OpenCSV** - Writes to the csv file.
- **UUID** - Generates the UID for the threads UIDs.

Note: This system was developped using IntelliJ IDEA 2021 and Java.

## Usage

To run the application you need to compile the file in `/src/main/java` and then run `core.java`.

Compiling is acheive by executing this command in any terminal:
```
javac -d ./out/ ./src/main/java/*.java
```

You can use any alternative method you feel more comfortable with.

After that you will need to go to the `out` directory and run `core.java`:
```
cd ./out
java core <inputDirectory> <outputDirectory> &
```
_**Note:** The `&` makes the app run the background._

After that the application will run forever (or until stopped manually). It will start by converting all files it finds in `inputDirectory` from wav to mp3 format. After that, it will monitor the `inputDirectory` and convert any new added or modified wav file to mp3 format automatically. All converted files will be stored in `outputDirectory`.

Any error will be logged to `logs.txt` so you can check that file to see if any problems was encountered.
Any successful process will be stored into `processes.csv` so that it records all files that were ever converted.

***Notes:***
- Currently (Release 1.0) when you run the app it will generate an error: `SL4J`, it does not affect any conversion and poses no problem to the functionality of the application.
- To change any of the names of the files that records errors or processes you will need to go into the code of the `Logger` class and modify them manually (I left marks as to where to edit to change those names).
- There are default values for both `inputDirectory` and `outputDirectory` respectively being: `./src/main/resources/input` and `./src/main/resources/output`. Those directories are within the project and can be changed to whatever you want them to from either `core.java` or the command line. I strongly advise not changing those default values.
- You can add files to the input directory at any point after or before the launch of the app and it will detect them and convert them as long as they are wav files. Any other format will be rejected and outputs an error with the name of the file.
- In `./src/main/resources/input` there are sample *wav* files to test out the application but you add some if you so desire.

## Possible Future Improvements

After finishing this initial version of the app, I can point out a few design improvements that could be made to improve performances:

- Make the multithreading part of the app more efficient ot reduce the time it takes to convert the whole directory in one go (if it has already files at launch).
- Create a `File` object to simplify some parts of the methods used to setup the app before converting files to mp3.
