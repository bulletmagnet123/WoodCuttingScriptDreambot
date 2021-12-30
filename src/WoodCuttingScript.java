import com.sun.source.tree.Tree;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.DropPattern;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.SkillTracker;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Model;
import org.dreambot.api.wrappers.widgets.message.Message;
import picocli.CommandLine;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.dreambot.api.methods.interactive.GameObjects.all;

@ScriptManifest(category = Category.WOODCUTTING, name = "Bullets Woodcutter", author = "Bulletmagnet", version = 1.0)
public class WoodCuttingScript extends AbstractScript implements ChatListener {

    Area YewTrees = new Area(2717, 3467, 2704, 3457);
    Area MagicTrees = new Area(2707, 3395, 2698, 3399);
    Area ArdougnBankLocation = new Area(2621, 3330, 2611, 3335);
    Area LevelChop = new Area(2332, 3051, 2336, 3046);
    Area WillowLevel = new Area(3056, 3250, 3063, 3255);
    String axe = "Rune axe";

    GameObject treeStump = null;
    private Timer timeRan;
    int beginningXP;
    int currentXp;
    int xpGained;
    private final BasicStroke stroke1 = new BasicStroke(5);
    private DrawMouseUtil drawMouseUtil = new DrawMouseUtil();
    public int TreesChopped = 0;
    public ZenAntiBan antiban;
    public String LevelOrMoneyOrWillowLevel;

    public String BankOrDrop;


    boolean StartScript = false;


    @Override
    public void onMessage(Message m) {
        if (m.getMessage().contains("You get some")) {
            cut++;
        }
    }


    public void onStart() {
        antiban = new ZenAntiBan(this);
        drawMouseUtil.setRandomColor();
        timeRan = new Timer();
        SkillTracker.start();
        beginningXP = Skills.getExperience(Skill.WOODCUTTING);
        GUI();
        if (StartScript) {
            if (hasEquipment() && Inventory.onlyContains(axe)) {
                chop();
            } else {
                bank();
            }
        }
    }

    public void bank() {

        if (hasEquipment() && !Inventory.isFull()) {
            chop();
        } else if (Inventory.isEmpty()) {

            Walking.walk(Bank.getClosestBankLocation());
            sleep(1000, 2500);
            Bank.openClosest();
            sleep(500, 1200);
            Bank.withdraw(axe, 1);
            sleep(500, 1000);
            Bank.close();
            if (hasEquipment() && !Inventory.isFull()) {
                chop();
            }
        } else if (Inventory.isFull()) {

            Walking.walk(Bank.getClosestBankLocation());
            sleep(1000, 2500);
            Bank.openClosest();
            sleep(500, 1200);
            Bank.depositAllExcept("Dragon axe");
            Bank.close();
            Walking.walk(Bank.getClosestBankLocation());
        } else if (Inventory.onlyContains(axe)) {
            chop();
        } else {
            chop();
        }
    }

    public void drop() {
        sleep(500, 1300);
        Inventory.dropAllExcept(axe);
        sleep(1000, 2500);
    }

    public void chop() {
        if (LevelOrMoneyOrWillowLevel == "LEVEL") {
            GameObject TeakTree = GameObjects.closest("Teak");
            if (Inventory.isFull()) {
                if (BankOrDrop == "BANK") {
                    bank();
                } else if (BankOrDrop == "DROP") {
                    drop();
                }
            } else {
                if (!LevelChop.contains(getLocalPlayer())) {
                    sleep(1000, 2500);
                    Walking.walk(LevelChop.getRandomTile());
                    sleep(1280, 2700);
                } else if (LevelChop.contains(getLocalPlayer())) {
                    if (TeakTree != null) {
                        Camera.rotateToEntity(TeakTree);
                        sleep(1000, 1500);
                        TeakTree.interact("Chop down");
                        sleep(1500, 2500);
                        while (getLocalPlayer().isAnimating()) {
                            antiban.antiBan();
                        }
                        sleepUntil(() -> getLocalPlayer().getAnimation() == -1, 240000);
                    }
                }
            }
        } else if (LevelOrMoneyOrWillowLevel == "MONEY") {
            GameObject MagicTree = GameObjects.closest("Magic tree");
            if (Inventory.isFull()) {
                if (BankOrDrop == "BANK") {
                    bank();
                } else if (BankOrDrop == "DROP") {
                    drop();
                }
                if (!MagicTrees.contains(getLocalPlayer())) {
                    sleep(1000, 2500);
                    Walking.walk(MagicTrees.getRandomTile());
                    sleep(1280, 2700);
                } else if (MagicTrees.contains(getLocalPlayer())) {
                    if (MagicTree != null) {
                        Camera.rotateToEntity(MagicTree);
                        sleep(1000, 1500);
                        MagicTree.interact("Chop down");
                        sleep(1500, 2500);
                        while (getLocalPlayer().isAnimating()) {
                            antiban.antiBan();
                        }
                        sleepUntil(() -> getLocalPlayer().getAnimation() == -1, 240000);
                    }
                }
            }
        } else if (Objects.equals(LevelOrMoneyOrWillowLevel, "WillowLevel")) {
            GameObject Willow = GameObjects.closest("Willow");
            if (Inventory.isFull()) {
                if (BankOrDrop == "BANK") {
                    bank();
                } else if (BankOrDrop == "DROP") {
                    drop();
                }
            } else {
                if (!WillowLevel.contains(getLocalPlayer())) {
                    sleep(1000, 2500);
                    Walking.walk(WillowLevel);
                    sleep(1280, 2700);
                } else if (WillowLevel.contains(getLocalPlayer())) {
                    if (Willow != null) {
                        Camera.rotateToEntity(Willow);
                        sleep(1000, 1500);
                        Willow.interact("Chop down");
                        sleep(1500, 2500);
                        while (getLocalPlayer().isAnimating()) {
                            antiban.antiBan();
                        }
                        sleepUntil(() -> getLocalPlayer().getAnimation() == -1, 240000);
                    }
                }
            }
        }
    }


    public boolean hasEquipment() {
        boolean yes;
        if (Inventory.contains(axe)) {
            yes = true;
        } else {
            yes = false;
        }
        return yes;
    }


    public int onLoop() {
        if (StartScript) {
            if (hasEquipment() && !Inventory.isFull()) {
                chop();
            } else {
                if (BankOrDrop == "BANK") {
                    bank();
                } else if (BankOrDrop == "DROP") {
                    drop();
                }
            }
        }
        return 0;

    }

    public void GUI() {
        JFrame frame = new JFrame();
        frame.setTitle("BulletsChopper");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(300, 170));
        frame.pack();
        frame.setVisible(true);

        JPanel settingPanel = new JPanel();
        frame.add(settingPanel);


        settingPanel.setLayout(new GridLayout(0, 2));
        JLabel Mode = new JLabel();
        Mode.setText("Bank or Drop");
        settingPanel.add(Mode);
        JComboBox<String> BankorDopBox = new JComboBox<>(new String[]{
                "BANK", "DROP"
        });
        settingPanel.add(BankorDopBox);
        JComboBox<String> LevelOrMoneyOrWillowLevelBox = new JComboBox<>(new String[]{
                "LEVEL", "MONEY", "WillowLevel"
        });
        settingPanel.add(LevelOrMoneyOrWillowLevelBox);


        JButton start = new JButton();
        settingPanel.add(start);

        start.setText("Start");
        start.addActionListener(l -> {
            BankOrDrop = BankorDopBox.getSelectedItem().toString();
            LevelOrMoneyOrWillowLevel = LevelOrMoneyOrWillowLevelBox.getSelectedItem().toString();


            StartScript = true;
            frame.dispose();
        });
    }

    public int cut;


    public void onPaint(Graphics2D g) {
        long ttl = SkillTracker.getTimeToLevel(Skill.WOODCUTTING);
        long timeTNL = ttl;
        g.setColor(new Color(51, 51, 51, 140));
        g.drawRect(10, 250, 300, 400);
        g.fillRect(10, 250, 300, 400);
        g.setStroke(stroke1);
        Timer.formatTime(ttl);
        Polygon tile = Map.getPolygon(getLocalPlayer().getTile());
        g.drawPolygon(tile);
        currentXp = Skills.getExperience(Skill.WOODCUTTING);
        xpGained = currentXp - beginningXP;
        SkillTracker.getTimeToLevel(Skill.WOODCUTTING);
        g.setColor(Color.CYAN);
        g.drawString("Bullets Chopper", 140, 280);
        g.drawString("Anti-Ban Status: " + (antiban.getStatus().equals("") ? "Inactive" : antiban.getStatus()), 150, 320);
        g.drawString("current xp: " + currentXp, 150, 450);
        g.drawString("Trees Chopped: " + cut, 150, 425);
        g.drawString("Time Ran: " + timeRan.formatTime(), 150, 400);
        g.drawString("XP GAINED: " + xpGained, 150, 375);
        g.drawString("Current level: " + Skills.getRealLevel(Skill.WOODCUTTING), 150, 350);
        g.drawString("Time tell level: " + ft(ttl), 20, 300);
        g.drawString("bank or drop:  " + BankOrDrop, 15, 320);
        g.drawString("Level or Money: " + LevelOrMoneyOrWillowLevel, 15, 340);
        drawMouseUtil.drawRandomMouse(g);
        drawMouseUtil.drawRandomMouseTrail(g);
        int i = 0;
        for (i = 0; i < 10; i++ ){
            List<GameObject> Tree = all("Willow");
            Tree.get(i).getModel().drawWireFrame(g);
        }

    }

    private String ft(long duration) {
        String res = "";
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                .toHours(duration));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                .toMinutes(duration));
        if (days == 0) {
            res = (hours + ":" + minutes + ":" + seconds);
        } else {
            res = (days + ":" + hours + ":" + minutes + ":" + seconds);
        }
        return res;
    }


}


