import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

public class Albumizer {

	public static void main(String[] args) {
//		String sourceFolder = "S:\\Backup\\Lenovo K3 Note\\DCIM\\From others";
		String sourceFolder = "X:\\Memories\\Yercaud\\photos\\ram anna mobile";
		String destinationFolder = "X:\\Memories\\New Album\\";
		File file = new File(sourceFolder);
		long fileSize = 0L;
		boolean filesOnly = true;
		String dateTakenText = null;
		SimpleDateFormat sdf;
		File destionationFile;
		Path sourceFilePath;
		File image;
		BasicFileAttributes attrs;
		Metadata metadata;
		Date dateTaken;
		Calendar calendar;
		String directoryName;
		File directoryCreator;
		String skipedFiles = "";
		Date startTime = new Date();
//		Scanner scanner = new Scanner(System.in);
//		System.out.println("Enter Source Folder Path (E.g: \"S:\\Backup\\Lenovo K3 Note\\DCIM\\From others\") :");
//		sourceFolder = scanner.nextLine().replaceAll("\\", "\\\\");
//		System.out.println("Enter Source Folder Path (E.g: \"X:\\Memories\\New Album\\ \") :");
//		destinationFolder = scanner.nextLine().replaceAll("[\\]", "\\\\");
		
		for (String fileName : file.list()) {
			// if ((fileSize / 1000000) <= 100) {
			dateTakenText = null;
			sourceFilePath = Paths.get(sourceFolder, fileName);
			try {
				if (Files.isRegularFile(sourceFilePath) && filesOnly) {
					attrs = Files.readAttributes(sourceFilePath, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
					fileSize += attrs.size();

					image = sourceFilePath.toFile();
					sdf = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
					try {
						metadata = ImageMetadataReader.readMetadata(image);
						dateTakenText = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class).getString(306);
					} catch (ImageProcessingException e) {
						System.out.println(fileName);
					} catch (NullPointerException e) {
						System.out.println(fileName);
						skipedFiles += "\n" + fileName;
						continue;
					}

					if (dateTakenText == null) {
						dateTakenText = attrs.lastModifiedTime().toString();
						sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
					}

					dateTaken = sdf.parse(dateTakenText);
					calendar = Calendar.getInstance();
					calendar.setTime(dateTaken);
					directoryName = Month.of(calendar.get(Calendar.MONTH) + 1).getDisplayName(TextStyle.SHORT,
							Locale.US) + " " + calendar.get(Calendar.YEAR) + "\\" + "From Others";
					directoryCreator = new File(destinationFolder + directoryName);
					if (!directoryCreator.exists()) {
						directoryCreator.mkdir();
					}
					destionationFile = new File(destinationFolder, directoryName + "\\" + fileName);
					if (!destionationFile.exists()) {
						Files.copy(sourceFilePath, destionationFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
						System.out.println("Copied " + fileName);
					} else {
						System.out.println(fileName + " already exists");
					}
				}
			} catch (IOException e) {
				System.out.println(fileName);
				e.printStackTrace();
			} catch (ParseException e) {
				System.out.println(fileName);
				e.printStackTrace();
			} finally {
				System.out.println("\nTotal Size: " + fileSize / 1000000 + "MB");
				System.out.println("Skipped Files: " + skipedFiles);
			}

		}
		

		Date endTime = new Date();

		long msTaken = endTime.getTime() - startTime.getTime();

		long totalSecondsTaken = msTaken / 1000;
		long minutesTaken = totalSecondsTaken / 60;
		long secondsTaken = totalSecondsTaken % 60;

		System.out.println("\nTime Taken: " + minutesTaken + " minutes and " + secondsTaken + " seconds.");

	}
}
