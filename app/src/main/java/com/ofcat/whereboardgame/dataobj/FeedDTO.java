package com.ofcat.whereboardgame.dataobj;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by orangefaller on 2017/1/14.
 */

public class FeedDTO {

    @SerializedName("updated")
    private TstringDTO updated;

    @SerializedName("title")
    private TypeDTO sheetTitle;

    @SerializedName("category")
    private ArrayList<CategoryDTO> categoryDTOs;

    @SerializedName("author")
    private ArrayList<AuthorDTO> authorDTOs;

    @SerializedName("entry")
    private ArrayList<EntryDTO> entryDTOs;

    public TstringDTO getUpdated() {
        return updated;
    }

    public void setUpdated(TstringDTO updated) {
        this.updated = updated;
    }

    public ArrayList<CategoryDTO> getCategoryDTOs() {
        return categoryDTOs;
    }

    public void setCategoryDTOs(ArrayList<CategoryDTO> categoryDTOs) {
        this.categoryDTOs = categoryDTOs;
    }

    public TypeDTO getSheetTitle() {
        return sheetTitle;
    }

    public void setSheetTitle(TypeDTO sheetTitle) {
        this.sheetTitle = sheetTitle;
    }

    public ArrayList<AuthorDTO> getAuthorDTOs() {
        return authorDTOs;
    }

    public void setAuthorDTOs(ArrayList<AuthorDTO> authorDTOs) {
        this.authorDTOs = authorDTOs;
    }

    public ArrayList<EntryDTO> getEntryDTOs() {
        return entryDTOs;
    }

    public void setEntryDTOs(ArrayList<EntryDTO> entryDTOs) {
        this.entryDTOs = entryDTOs;
    }

    public class AuthorDTO {

        @SerializedName("name")
        private TstringDTO name;

        @SerializedName("email")
        private TstringDTO email;

        public TstringDTO getName() {
            return name;
        }

        public void setName(TstringDTO name) {
            this.name = name;
        }

        public TstringDTO getEmail() {
            return email;
        }

        public void setEmail(TstringDTO email) {
            this.email = email;
        }
    }
}
