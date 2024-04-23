package io.chat.live.json;

import io.chat.live.domain.RoomEventData;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.SqlTypes;
import org.hibernate.usertype.UserType;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class RoomEventDataType implements UserType<RoomEventData> {

    private final JsonSerializer serializer = new JsonSerializer();

    @Override
    public int getSqlType() {
        return SqlTypes.JSON;
    }

    @Override
    public Class<RoomEventData> returnedClass() {
        return RoomEventData.class;
    }

    @Override
    public boolean equals(RoomEventData x, RoomEventData y) {
        return x == null || x.equals(y);
    }

    @Override
    public int hashCode(RoomEventData x) {
        return x.hashCode();
    }

    @Override
    public RoomEventData nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        try {
            var cell = rs.getString(position);
            if (cell == null) {
                return null;
            }
            var clazz = returnedClass();
            return serializer.fromJson(cell, clazz);
        } catch (Exception e) {
            var msg = "Failed to convert String to %s: %s"
                .formatted(getTargetedClassName(), e.getMessage());
            throw new SQLException(msg, e);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, RoomEventData data, int index, SharedSessionContractImplementor session) throws SQLException {
        try {
            if (data == null) {
                st.setNull(index, Types.OTHER);
                return;
            }
            var json = serializer.toJson(data);
            st.setObject(index, json, Types.OTHER);
        } catch (Exception e) {
            var msg = "Failed to convert %s to String: %s"
                .formatted(getTargetedClassName(), e.getMessage());
            throw new SQLException(msg, e);
        }
    }

    @Override
    public RoomEventData deepCopy(RoomEventData value) {
        try {
            // Use serialization to materialize a clone
            var bos = new ByteArrayOutputStream();
            var oos = new ObjectOutputStream(bos);
            oos.writeObject(value);
            oos.flush();
            oos.close();
            bos.close();

            var bais = new ByteArrayInputStream(bos.toByteArray());
            var obj = (RoomEventData) new ObjectInputStream(bais).readObject();
            bais.close();
            return obj;

        } catch (ClassNotFoundException | IOException ex) {
            throw new HibernateException(ex);
        }
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(RoomEventData value) {
        return null;
    }

    @Override
    public RoomEventData assemble(Serializable cached, Object owner) {
        return null;
    }

    private String getTargetedClassName() {
        return RoomEventData.class.getSimpleName();
    }
}
