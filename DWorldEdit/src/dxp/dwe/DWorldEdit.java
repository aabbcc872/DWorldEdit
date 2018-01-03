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
  String lv=player.getLevel().getFolderName();
  Item item=event.getItem();
  Block block=event.getBlock();
  Vector3 v3=new Vector3(block.x,block.y,block.z);
  switch(item.getId()){
   case 280:
    if(st=="pos1" || st=="wait"){
     data.put("pos1",v3);
     data.put("level",lv);
     this.msg(player,"§a基点1设置成功"+v3.toString());
     st="pos2";
    }else if(st=="pos2"){
     data.put("pos2",v3);
     if(!(((String)data.get("level")).equals(lv))){
      this.msg(player,"§4基点2与基点1所在世界不同");
     }
     data.put("level",lv);
     this.msg(player,"§a基点2设置成功"+v3.toString());
     st="wait";
    }else{
     this.msg(player,"§4当前正在使用其他工具，禁止更改基点");
     this.msg(player,"§e如要重新选择基点，请使用非创世神工具的物品破坏任意方块");
    }
    event.setCancelled();
    break;
   case 270:
    if(!data.containsKey("pos1") || !data.containsKey("pos2")){
     this.msg(player,"§4请先设置坐标基点");
     event.setCancelled();
     return;
    }
    if(st=="wait"){
     this.msg(player,"§a已进入设置模式");
     data.put("id",block.getId());
     data.put("meta",block.getDamage());
     this.msg(player,"§e设置区域内所有方块为 "+block.getId()+":"+block.getDamage()+" 再次点击以生成");
     st="set";
    }else if(st=="set"){
     SetThread set=new SetThread(this,player,data);
     set.start();
     st="wait";
    }else{
     this.msg(player,"§4当前未设置基点，禁止使用其他工具");
    }
    event.setCancelled();
    break;
   case 271:
    if(!data.containsKey("pos1") || !data.containsKey("pos2")){
     this.msg(player,"§4请先设置坐标基点");
     event.setCancelled();
     return;
    }
    if(st=="wait"){
     this.msg(player,"§a已进入替换模式");
     data.put("id",block.getId());
     data.put("meta",block.getDamage());
     this.msg(player,"§e被替换方块为 "+block.getId()+":"+block.getDamage());
     this.msg(player,"§e请选择目标替换方块");
     st="rp1";
    }else if(st=="rp1"){
     data.put("id1",block.getId());
     data.put("meta1",block.getDamage());
     this.msg(player,"§e替换区域内所有 "+data.get("id")+":"+data.get("meta")+"为"+block.getId()+":"+block.getDamage()+" 再次点击以生成");
     st="rp";
    }else if(st=="rp"){
     ReplaceThread rp=new ReplaceThread(this,player,data);
     rp.start();
     st="wait";
    }else{
     this.msg(player,"§4当前未设置基点，禁止使用其他工具");
    }
    event.setCancelled();
    break;
   case 290:
    if(!data.containsKey("pos1") || !data.containsKey("pos2")){
     this.msg(player,"§4请先设置坐标基点");
     event.setCancelled();
     return;
    }
    if(st=="wait"){
     this.msg(player,"§a进入复制模式");
     data.put("pos3",v3);
     this.msg(player,"§a基点3设置成功"+v3.toString());
     this.msg(player,"§e请选择基点4(目标复制区域定位基点)");
     st="cp1";
    }else if(st=="cp1"){
     data.put("pos4",v3);
     data.put("level1",lv);
     this.msg(player,"§a基点4设置成功"+v3.toString());
     this.msg(player,"§e复制所选区域，以基点 "+((Vector3)data.get("pos3")).toString()+" 到基点 "+v3.toString());
     if(!(((String)data.get("level")).equals(lv))){
      this.msg(player,"§e以世界 "+(String)data.get("level")+" 到世界 "+lv);
     }
     this.msg(player,"§e再次点击以生成，默认不复制空气，若要复制空气，请点击石头(id:1)");
     st="cp2";
    }else if(st=="cp2"){
     if(block.getId()==1){
      data.put("copyair",true);
     }else{
      data.put("copyair",false);
     }
     CopyThread cp=new CopyThread(this,player,data);
     cp.start();
     st="wait";
    }else{
     this.msg(player,"§4当前未设置基点，禁止使用其他工具");
    }
    event.setCancelled();
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
  if(this.data.containsKey(name)){
   HashMap data=(HashMap)this.data.get(name);
   if(data.containsKey("pos1")){
    HashMap dt=new HashMap();
    dt.put("status","pos1");
    this.data.put(name,dt);
    this.msg(player,"§a成功删除创世神状态数据");
   }
  }
 }
 
 public void msg(Player player,String message){
  player.sendMessage("§fDWE*"+message);
 }
}
