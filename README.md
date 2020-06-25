# batch-converter-and-publisher
Very fast audio to Youtube video batch converter and publisher.


Optimised for very large collections of audio files. Put terrabytes of audio files and your prefered background images into a folder. Set the path name. Click run.

Already working: Batch conversion of several audio files and one image to mkv files. Each converted mkv file has the name consisting of the related audio file meta datas title attribute followed by it's artist attribute. Optionally, an xml or json file can be provided, which holds the informations of the provided audio files, like for example used for collections of audio files on https://archive.org/ .

Possible extensions: Direct uploads of the converted files to a Youtubechannel, adjusting all Youtube attributes to predefined inputs. Combined publishing on further social network sites. Customizeable for all related automation processes. GUI interface.


# How to use it right now
Download an audio collection from https://archive.org/ 
Choose an image as background.
Either download the xml file of the collection from https://archive.org/ as well, or give it a try without it.
Put all extracted audio files and the image file into a folder.
Adjust the path in Converter.class to your folder.
Compile and run the code.
