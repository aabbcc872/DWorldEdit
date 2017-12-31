package dxp.dwe;

import java.util.Map;
import java.util.HashMap;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.block.Block;
import cn.nukkit.math.Vector3;

public class SetThread extends Thread{
 private DWorldEdit plugin;
 public Player player;
 private int bid=0;
 private int bmeta=0;
 private Vector3 pos1;
 private Vector3 pos2;
 private String level;
 
 public SetThread(DWorldEdit pl,Player player,HashMap data){
  this.plugin=pl;
  this.player=player;
  this.pos1=(Vector3)data.get("pos1");
  this.pos2=(Vector3)data.get("pos2");
  this.bid=(int)data.get("id");
  this.bmeta=(int)data.get("meta");
  this.level=(String)data.get("level");
 }

 public void run(){
  this.SetBlocks();
 }
 
 public void SetBlocks(){
  Level level=this.plugin.getServer().getLevelByName(this.level);
  Vector3 p1=this.pos1;
  Vector3 p2=this.pos2;
  int sx = Math.min((int)p1.x,(int)p2.x);
  int ex = Math.max((int)p1.x,(int)p2.x);
  int sz = Math.min((int)p1.z,(int)p2.z);
  int ez = Math.max((int)p1.z,(int)p2.z);
  int sy = Math.min((int)p1.y,(int)p2.y);
  int ey = Math.max((int)p1.y,(int)p2.y);
  int blocks=0;
  long time = System.currentTimeMillis();
  for(int nx=sx;nx <= ex;nx++){
   for(int nz=sz;nz <= ez;nz++){
    for(int ny=sy;ny <= ey;ny++){
     blocks++;
     Block b=Block.get(this.bid,this.bmeta);
     level.setBlock(new Vector3(nx,ny,nz),b,true,true);
    }
   }
  }
  time=System.currentTimeMillis()-time;
  this.player.sendMessage("§b生成完毕 方块"+this.bid+":"+this.bmeta+" 数量"+blocks+" 耗时"+time+"ms");
 }
}
