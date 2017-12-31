package dxp.dwe;

import java.util.ArrayList;
import java.util.HashMap;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.math.Vector3;
import cn.nukkit.item.Item;
import cn.nukkit.block.Block;
import cn.nukkit.plugin.PluginBase;

public class DWorldEdit extends PluginBase implements Listener{
 public HashMap data=new HashMap();
 
 @Override
 public void onEnable(){
  this.getServer().getPluginManager().registerEvents(this,this);
  getLogger().info("§a创世神加载完成!");
 }
 
 @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
 public void onTouch(PlayerInteractEvent event){
  Player player=event.getPlayer();
  String name=player.getName();
  if(!player.isOp()){
   return;
  }
  if(!this.data.containsKey(name)){
   HashMap dt=new HashMap();
   dt.put("status","pos1");
   this.data.put(name,dt);
  }
  HashMap data=(HashMap)this.data.get(name);
  String st=(String)data.get("status");
  Item item=event.getItem();
  Block block=event.getBlock();
  Vector3 v3=new Vector3(block.x,block.y,block.z);
  switch(item.getId()){
   case 280:
    if(st=="pos1" || st=="wait"){
     data.put("pos1",v3);
     data.put("level",player.getLevel().getName());
     this.msg(player,"§a基点1设置成功"+v3.toString());
     st="pos2";
    }else if(st=="pos2"){
     data.put("pos2",v3);
     data.put("level",player.getLevel().getName());
     this.msg(player,"§a基点2设置成功"+v3.toString());
     st="wait";
    }
    event.setCancelled();
    break;
   case 270:
    if(!data.containsKey("pos1") || !data.containsKey("pos2")){
     this.msg(player,"§4请先设置坐标基点");
     return;
    }
    if(st=="wait"){
     data.put("id",block.getId());
     data.put("meta",block.getDamage());
     this.msg(player,"§e设置区域内所有方块为"+block.getId()+":"+block.getDamage()+"再次点击以生成");
     st="set";
     event.setCancelled();
    }else if(st=="set"){
     SetThread set=new SetThread(this,player,data);
     set.start();
     st="wait";
     event.setCancelled();
    }
    break;
   case 271:
    if(!data.containsKey("pos1") || !data.containsKey("pos2")){
     this.msg(player,"§4请先设置坐标基点");
     return;
    }
    if(st=="wait"){
     data.put("id",block.getId());
     data.put("meta",block.getDamage());
     this.msg(player,"§e被替换方块为"+block.getId()+":"+block.getDamage());
     st="rp1";
     event.setCancelled();
    }else if(st=="rp1"){
     data.put("id1",block.getId());
     data.put("meta1",block.getDamage());
     this.msg(player,"§e替换区域内所有"+data.get("id")+":"+data.get("meta")+"为"+block.getId()+":"+block.getDamage()+"再次点击以生成");
     st="rp";
     event.setCancelled();
    }else if(st=="rp"){
     ReplaceThread rp=new ReplaceThread(this,player,data);
     rp.start();
     st="wait";
     event.setCancelled();
    }
    break;
  }
  data.put("status",st);
  this.data.put(name,data);
 }
 
 @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
 public void onBreak(BlockBreakEvent event){
  Player player=event.getPlayer();
  String name=player.getName();
  if(!player.isOp()){
   return;
  }
  HashMap dt=new HashMap();
  dt.put("status","pos1");
  this.data.put(name,dt);
 }
 
 public void msg(Player player,String message){
  player.sendMessage("§fDWE*"+message);
 }
}
