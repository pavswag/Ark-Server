package io.kyros.util;

import java.io.*;

public class Buffer {
    private ByteArrayOutputStream byteArrayOutputStream;
    private ByteArrayInputStream byteArrayInputStream;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    public Buffer() {
        byteArrayOutputStream = new ByteArrayOutputStream();
        dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    }

    public Buffer(byte[] data) {
        byteArrayInputStream = new ByteArrayInputStream(data);
        dataInputStream = new DataInputStream(byteArrayInputStream);
    }

    public void writeInt(int value) throws IOException {
        dataOutputStream.writeInt(value);
    }

    public void writeUnsignedShort(int value) throws IOException {
        dataOutputStream.writeShort(value);
    }
    public void writeUnsignedByte(int value) throws IOException {
        dataOutputStream.writeByte(value);
    }

    public void writeStringCp1252NullTerminated(String value) throws IOException {
        dataOutputStream.write(value.getBytes());
        dataOutputStream.writeByte(0); // Null-terminated string
    }

    public byte[] toByteArray() {
        return byteArrayOutputStream.toByteArray();
    }

    public int readInt() throws IOException {
        return dataInputStream.readInt();
    }

    public int readUnsignedShort() throws IOException {
        return dataInputStream.readUnsignedShort();
    }

    public String readStringCp1252NullTerminated() throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        int ch;
        while ((ch = dataInputStream.readByte()) != 0) {
            result.write(ch);
        }
        return new String(result.toByteArray());
    }

    public int readUnsignedByte() throws IOException {
        return dataInputStream.readUnsignedByte();
    }

    public short readShort() throws IOException {
        return dataInputStream.readShort();
    }
}
