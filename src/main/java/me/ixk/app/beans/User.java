package me.ixk.app.beans;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    protected String name;
    protected int age;

    protected User2 user;
}
