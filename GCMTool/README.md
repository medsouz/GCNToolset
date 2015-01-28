GCMTool
=======
A tool to extract, modify, and repack Gamecube disk images

Usage
-----
`gcmtool info <file>` - Shows information about how things are stored in the image

`gcmtool extract <file> [outputDir=Game ID]` - Extracts the disk's contents to outputDir.

`gcmtool create <source> [outputFile=out.iso]` - Repacks a previously extracted game into outputFile.

Known Issues
------------
* Repacking only confirmed working for *Mario Kart: Double Dash!!*
  * Tested on *Super Smash Bros: Melee* and *Super Mario Sunshine*, could extract perfectly fine but failed to run after repacking. Haven't looked into fixing it yet but GCReEx (The only other repacker I know of) seems to have the same issue.
