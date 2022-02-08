import java.awt.Color

import eu.mihosoft.vrl.v3d.*
import eu.mihosoft.vrl.v3d.parametrics.*


xkey 		= new LengthParameter("Length",120,[240.0,40.0])
ykey 		= new LengthParameter("Width",30,[240.0,40.0])
zkey 		= new LengthParameter("Height",2,[30.0,0.0])
cutoutx 		= new LengthParameter("Cutout Length",45,[120.0,5.0])
cutouty		= new LengthParameter("Cutout Width",13,[120.0,5.0])
magnetDiameter = new LengthParameter("Magnet Diameter", 8.15, [20.0,0.0])
magnetDepth 	= new LengthParameter("Magnet Depth",1.0,[5.0,0.0])
doveTailWidth = new LengthParameter("Dove Tail Width",5,[80.0,5.0])
doveTailHeight = new LengthParameter("Dove Tail Height",5,[80.0,5.0])
doveTailScale = new LengthParameter("Connector Tolerance",0.2,[2.0,0.0])

CSG makeHolder(String name = ""){
	def cube = new Cube(xkey,ykey,zkey).toCSG()
	def dovetail = makeDoveTail()
	def o = doveTailScale.getMM()/2
	cube = cube.difference(dovetail.movey(-ykey.getMM()/2-o))
	cube = cube.difference(dovetail.movey(-ykey.getMM()/2-o).rotz(180))
	cube = cube.difference(dovetail.movey(-xkey.getMM()/2-o).rotz(90))
	cube = cube.difference(dovetail.movey(-xkey.getMM()/2-o).rotz(270))
	def magnet = makeMagnet().movez(zkey.getMM()/2-magnetDepth.getMM())
	magnet = magnet.move(xkey.getMM()/2-3*magnetDiameter.getMM()/4,ykey.getMM()/2-3*magnetDiameter.getMM()/4,0);
	magnet = magnet.union(magnet.mirrorx())
	magnet = magnet.union(magnet.mirrory())
	cube = cube.difference(magnet)
	def cutout = new Cube(cutoutx.getMM(),cutouty.getMM(),zkey.getMM()).toCSG()
	cube = cube.difference(cutout)
	cube = cube.roty(180)
	Font font = new Font("Arial Bold",10);
	def text = TextExtrude.text((double)2.0,name,font).collect{
			it.rotx(180)
			.movex(-xkey.getMM()/2+1)
			.movey(ykey.getMM()/2-10)
			.movez(1.0)
			.toZMin()}
	cube = cube.union(text)
	return cube
		.setParameter(cutoutx)
		.setParameter(cutouty)
		.setParameter(magnetDiameter)
		.setParameter(magnetDepth)
		.setRegenerate({ makeHolder()})
}

CSG makeConnector(){
//	def h = doveTailHeight.getMM()
//	def w = doveTailWidth.getMM()
	def o = doveTailScale.getMM()
	CSG dovetail = makeDoveTail(o)
	return dovetail.union(dovetail.rotz(180))
//	.scalex((w-o)/w)
//	.scaley((h-o)/h)
	.setParameter(doveTailWidth)
		.setParameter(doveTailHeight)
		.setParameter(doveTailScale)
		.setRegenerate({makeConnector()})
}

CSG makeDoveTail(double o = 0) {
	def h = doveTailHeight.getMM()/2
	def w = doveTailWidth.getMM()/2
	return Extrude.points(new Vector3d(0, 0, zkey.getMM()),
                new Vector3d(-w/6+o,0),
                new Vector3d(-w/2+2*o,h-o),
                new Vector3d(w/2-2*o,h-o),
                new Vector3d(w/6-o,0)
        ).movez(-(zkey.getMM()/2));
}
	
CSG makeMagnet(){
	def rad = magnetDiameter.getMM()/2
	def depth = magnetDepth.getMM()
	def cylinder = new Cylinder(rad,depth).toCSG()
	def seamNotches = new Cube(rad*1.6,rad*1.6,depth).toCSG()//should add corners to magnet holes where the slicer will hopefully hide the seam that was causing the parts to be out of tolerance
	return cylinder.union(seamNotches.movez(depth/2))
}

CSG holder = makeHolder().setColor(javafx.scene.paint.Color.BLUE).movey(-doveTailScale.getMM()/2).setName("Blank Holder");
CSG holder1 = makeHolder("1001").setColor(javafx.scene.paint.Color.CYAN).movey(ykey.getMM()+doveTailScale.getMM()/2).setName("1001 Holder");
CSG holder2 = makeHolder("200n").setColor(javafx.scene.paint.Color.GREEN).movex(-xkey.getMM()-doveTailScale.getMM()/2).setName("200n Holder");
CSG holder3 = makeHolder("300n").setColor(javafx.scene.paint.Color.ORANGE).movey(-ykey.getMM()-doveTailScale.getMM()/2).setName("300n Holder");
CSG holderl = makeHolder("LA").setColor(javafx.scene.paint.Color.PURPLE).movex(xkey.getMM()+doveTailScale.getMM()/2).setName("LA Holder");
CSG holderp = makeHolder("Professor").setColor(javafx.scene.paint.Color.WHITE).movex(2*(xkey.getMM()+doveTailScale.getMM()/2)).setName("Professor Holder");

CSG conn = makeConnector()
			.setColor(javafx.scene.paint.Color.RED);

BowlerStudioController.addCsg(holder)
BowlerStudioController.addCsg(holder1)
BowlerStudioController.addCsg(holder2)
BowlerStudioController.addCsg(holder3)
BowlerStudioController.addCsg(holderl)
BowlerStudioController.addCsg(holderp)

BowlerStudioController.addCsg(conn.movey(ykey.getMM()/2).setName("Connector"))
BowlerStudioController.addCsg(conn.movey(xkey.getMM()/2).rotz(90).setManufacturing({ toMfg -> return null;}))
BowlerStudioController.addCsg(conn.movey(ykey.getMM()/2).rotz(180).setManufacturing({ toMfg -> return null;}))
BowlerStudioController.addCsg(conn.movey(xkey.getMM()/2).rotz(270).setManufacturing({ toMfg -> return null;}))
//BowlerStudioController.addCsg(holder.movey(ykey.getMM()+ doveTailScale.getMM()/2).setColor(javafx.scene.paint.Color.CYAN))
return null
