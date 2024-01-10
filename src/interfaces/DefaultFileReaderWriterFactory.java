package interfaces;

public class DefaultFileReaderWriterFactory implements FileReaderWriterFactory {
   
    public FileReaderWriter createFileReaderWriter(int format, String name) {
        switch (format) {
            case FileReaderWriter.FMT_TXT:
                return new FileTxtReaderWriter(name);
            case FileReaderWriter.FMT_KV:
                return new FileKVReaderWriter(name);
            default:
                throw new IllegalArgumentException("Format not supported: " + format);
        }
    }
}