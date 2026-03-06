package com.edusync.model.dto.request;

import com.edusync.common.enums.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePostRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Content type is required")
    private ContentType contentType;

    private String description;
   
    private String codeContent;
    private String language;

    private String frontSide;
    private String backSide;
}
