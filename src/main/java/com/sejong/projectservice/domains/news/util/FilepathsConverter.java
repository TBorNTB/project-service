package com.sejong.projectservice.domains.news.util;

import com.sejong.projectservice.support.common.file.Filepaths;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FilepathsConverter implements AttributeConverter<Filepaths, String> {


    @Override
    public String convertToDatabaseColumn(Filepaths filepaths) {
        if (filepaths == null) {
            return null;
        }
        return filepaths.toString();
    }

    @Override
    public Filepaths convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return Filepaths.of(dbData);
    }
}
