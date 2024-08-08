package media.sounds;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class RecorderTest {
    public static void main(String[] args) {
        boolean stopped = false;
        TargetDataLine line;
        AudioFormat format = new AudioFormat(44100.0f, 16, 2, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class,
                format); // format is an AudioFormat object
        if (!AudioSystem.isLineSupported(info)) {
            // Handle the error ...

        }
        // Obtain and open the line.
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
        } catch (LineUnavailableException ex) {
            throw new RuntimeException(ex);
            // Handle the error ...
        }

// Assume that the TargetDataLine, line, has already
// been obtained and opened.
        ByteArrayOutputStream out  = new ByteArrayOutputStream();
        int numBytesRead;
        byte[] data = new byte[line.getBufferSize() / 5];

// Begin audio capture.
        line.start();

// Here, stopped is a global boolean set by another thread.
        while (!stopped) {
            // Read the next chunk of data from the TargetDataLine.
            numBytesRead =  line.read(data, 0, data.length);
            // Save this chunk of data.
            out.write(data, 0, numBytesRead);
        }
    }
}

class WaveAudioCompressor {
    public static void compressWaveAudio(String inputFilePath, String outputFilePath, AudioFormat.Encoding encoding
            , float sampleRate, int sampleSizeInBits, int channels, int frameSize, float frameRate) throws IOException, UnsupportedAudioFileException {
        File inputFile = new File(inputFilePath);
        File outputFile = new File(outputFilePath);

        // Create the desired AudioFormat
        AudioFormat audioFormat = new AudioFormat(encoding, sampleRate, sampleSizeInBits, channels, frameSize, frameRate, true);

        // Create an AudioInputStream from the input file
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputFile);

        // Create an AudioInputStream with the new AudioFormat
        AudioInputStream compressedAudioInputStream = new AudioInputStream(audioInputStream, audioFormat, audioInputStream.getFrameLength());

        // Write the compressed audio to the output file
        AudioSystem.write(compressedAudioInputStream, AudioFileFormat.Type.WAVE, outputFile);
    }

    public static void main(String[] args) {
        try {
            compressWaveAudio("input.wav", "output.wav", AudioFormat.Encoding.ALAW, 8000f, 8, 1, 1, 8000f);
        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }
}