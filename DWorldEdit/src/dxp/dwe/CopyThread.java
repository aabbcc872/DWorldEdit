package dxp.dwe;

import java.util.Map;
import java.util.HashMap;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.block.Block;
import cn.nukkit.math.Vector3;

public class CopyThread extends Thread{
 private DWorldEdit plugin;
 public Player player;
 private Vector3 pos1;
 private Vector3 pos2;
 private Vector3 pos3;
 private Vector3 pos4;
 private String level;
 private String level1;
 private boolean copyair;
 
 public CopyThread(DWorldEdit pl,Player player,HashMap data){
  this.plugin=pl;
  this.player=player;
  this.pos1=(Vector3)data.get("pos1");
  this.pos2=(Vector3)data.get("pos2");
  this.pos3=(Vector3)data.get("pos3");
  this.pos4=(Vector3)data.get("pos4");
  this.level=(String)data.get("level");
  this.level1=(String)data.get("level1");
  this.copyair=(boolean)data.get("copyair");
 }

 public void run(){
  this.Copy();
 }
 
 public void Copy(){
  Level level=this.plugin.getServer().getLevelByName(this.level);
  Level level1=this.plugin.getServer().getLevelByName(this.level1);
  Vector3 p1=this.minV3(this.pos1,this.pos2);
  Vector3 p2=this.maxV3(this.pos1,this.pos2);
  Vector3 p3=this.pos3;
  Vector3 p4=this.pos4;
  
  int blocks=0;
  long time=System.currentTimeMillis();
  for(int nx=(int)p1.x;nx<=(int)p2.x;nx++){
   for(int ny=(int)p1.y;ny<=(int)p2.y;ny++){
    for(int nz=(int)p1.z;nz<=(int)p2.z;nz++){
     Vector3 v3=new Vector3(nx,ny,nz);
     Vector3 dv=p4.subtract(p3);
     Block b=level.getBlock(v3);
     if(b.getId()==0 && !this.copyair){
      continue;
     }
     v3=v3.add(dv);
     b=b.clone();
     level1.setBlock(v3,b,true,true);
     blocks++;
    }
   }
  }
  time=System.currentTimeMillis()-time;
  this.player.sendMessage("§b复制完毕 数量"+blocks+" 耗时"+time+"ms");
 }
 
 public Vector3 minV3(Vector3 v31,Vector3 v32){
  int x=Math.min((int)v31.x,(int)v32.x);
  int y=Math.min((int)v31.y,(int)v32.y);
  int z=Math.min((int)v31.z,(int)v32.z);
  return new Vector3(x,y,z);
 }
 
 public Vector3 maxV3(Vector3 v31,Vector3 v32){
  int x=Math.max((int)v31.x,(int)v32.x);
  int y=Math.max((int)v31.y,(int)v32.y);
  int z=Math.max((int)v31.z,(int)v32.z);
  return new Vector3(x,y,z);
 }
}
