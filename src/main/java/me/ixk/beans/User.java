package me.ixk.beans;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.ixk.annotations.Bean;
import me.ixk.annotations.Log;
import me.ixk.annotations.Log2;
import me.ixk.annotations.Scope;

@Data
@NoArgsConstructor
@Bean
@Scope("prototype")
public class User {
    protected String name = "syfxlin";
    protected int age = 21;

    @Log
    @Log2
    public String makeName() {
        return this.name;
    }
}
