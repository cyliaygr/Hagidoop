package interfaces;

public interface FileReaderWriterFactory {
    FileReaderWriter createFileReaderWriter(int format, String name);
}