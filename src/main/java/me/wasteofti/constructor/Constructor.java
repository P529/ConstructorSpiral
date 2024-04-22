package me.wasteofti.constructor;

import me.wasteofti.constructor.modules.ConstructorSpiral;
import me.wasteofti.constructor.modules.TridentHax;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class Constructor extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Constructor");

    @Override
    public void onInitialize() {
        LOG.info("Initializing Meteor Addon Template");

        Modules.get().add(new ConstructorSpiral());
        Modules.get().add(new TridentHax());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "me.wasteofti.constructor";
    }
}
