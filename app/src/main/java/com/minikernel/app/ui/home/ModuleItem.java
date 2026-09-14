package com.minikernel.app.ui.home;

public class ModuleItem {

    private final String id;
    private final String iconLetter;
    private final String title;
    private final String description;

    public ModuleItem(String id, String iconLetter, String title, String description) {
        this.id = id;
        this.iconLetter = iconLetter;
        this.title = title;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getIconLetter() {
        return iconLetter;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
