import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
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
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.widgets.message.Message;

import java.awt.*;
import java.util.concurrent.TimeUnit;

@ScriptManifest(category = Category.WOODCUTTING, name = "Bullets Woodcutter", author = "Bulletmagnet", version = 1.0)
public class WoodCuttingScript extends AbstractScript implements ChatListener {

    Area YewTrees = new Area(2717, 3467, 2704, 3457);
    Area MagicTrees = new Area(2707, 3395, 2698, 3399);
    Area ArdougnBankLocation = new Area(2621, 3330, 2611, 3335);
    String axe = "Dragon axe";
    GameObject treeStump = null;
    int TreesChopped;
    private Timer timeRan;
    int beginningXP;
    int currentXp;
    int xpGained;
    private final Color color1 = new Color(51, 51, 51, 147);
    private final Color color2 = new Color(138, 54, 15);
    private final Color color3 = new Color(255, 255, 255);
    private final BasicStroke stroke1 = new BasicStroke(5);
    private DrawMouseUtil drawMouseUtil = new DrawMouseUtil();


    public void onStart() {
        drawMouseUtil.setRandomColor();
        timeRan = new Timer();
        SkillTracker.start();
        beginningXP = Skills.getExperience(Skill.WOODCUTTING);
        if (hasEquipment()){
            chop();
        } else {
            bank();
        }
    }
    @Override
    public void onGameMessage(Message message) {
        if (message.getMessage().contains("You get some magic logs")){
            TreesChopped++;
        }
    }
    public void bank(){
        if (hasEquipment() && !Inventory.isFull()) {
            chop();
        } else if (Inventory.isEmpty()) {
            Walking.walk(BankLocation.ARDOUGNE_NORTH);
            sleep(1000, 2500);
            Bank.openClosest();
            sleep(500, 1200);
            Bank.withdraw("Dragon axe", 1);
            sleep(500, 1000);
            Bank.close();
            if (hasEquipment()) {
                chop();
            }
        }
        if (Inventory.isFull()) {
            Walking.walk(BankLocation.ARDOUGNE_NORTH);
            sleep(1000, 2500);
            if (ArdougnBankLocation.contains(getLocalPlayer())){
                Bank.openClosest();
                sleep(500, 1200);
                Bank.depositAllExcept("Dragon axe");
                Bank.close();
            } else {
                Walking.walk(BankLocation.ARDOUGNE_NORTH);
            }
        }
    }
    public void chop(){
        GameObject MagicTree = GameObjects.closest("Magic tree");
            if (Inventory.isFull()){
                bank();
            } else {
                if (!MagicTrees.contains(getLocalPlayer())) {
                    sleep(1000, 2500);
                    Walking.walk(MagicTrees.getRandomTile());
                    sleep(1280, 2700);
                } else if (MagicTrees.contains(getLocalPlayer())){
                    if (MagicTree != null){
                        MagicTree.interact("Chop down");
                        sleep(1500, 2500);
                        Mouse.moveMouseOutsideScreen();
                        sleepUntil(() -> getLocalPlayer().getAnimation() == -1, 240000);
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



    @Override
    public int onLoop() {
        if (hasEquipment() && !Inventory.isFull()){
            chop();
        } else {
            bank();
        }

        return 0;
    }
    public void onPaint(Graphics2D g){
        long ttl = SkillTracker.getTimeToLevel(Skill.WOODCUTTING);
        long timeTNL = ttl;
        g.setColor(Color.CYAN);
        g.drawRect(10, 250, 300, 400);


        Timer.formatTime(ttl);
        Polygon tile = Map.getPolygon(getLocalPlayer().getTile());

        g.drawPolygon(tile);
        currentXp = Skills.getExperience(Skill.WOODCUTTING);
        xpGained = currentXp - beginningXP;
        SkillTracker.getTimeToLevel(Skill.WOODCUTTING);
        g.setColor(color1);
        g.fillRect(10, 250, 300, 350);
        g.setColor(color2);
        g.setStroke(stroke1);
        g.setColor(color3);
        g.drawString("Bullets Chopper", 140, 280);
        g.drawString("current xp: " + currentXp, 200, 450);
        g.drawString("Trees Chopped " + TreesChopped, 200, 425);
        g.drawString("Time Ran: " + timeRan.formatTime(), 200, 400);
        g.drawString("XP GAINED: " + xpGained, 200, 375);
        g.drawString("Current level: " + Skills.getRealLevel(Skill.WOODCUTTING), 200, 350);
        g.drawString("Time tell level: " + ft(ttl), 20,300);
        drawMouseUtil.drawRandomMouse(g);
        drawMouseUtil.drawRandomMouseTrail(g);

    }
    private String ft(long duration)
    {
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

