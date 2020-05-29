package me.ixk.app.beans;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.Log;
import me.ixk.framework.annotations.Log2;
import me.ixk.framework.annotations.Scope;

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
