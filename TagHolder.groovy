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

CSG makeHolder(){
	def cube = new Cube(xkey,ykey,zkey).toCSG()
	def dovetail = makeDoveTail()
	cube = cube.difference(dovetail.movey(-ykey.getMM()/2))
	cube = cube.difference(dovetail.movey(-ykey.getMM()/2).rotz(180))
	cube = cube.difference(dovetail.movey(-xkey.getMM()/2).rotz(90))
	cube = cube.difference(dovetail.movey(-xkey.getMM()/2).rotz(270))
	def magnet = makeMagnet().movez(zkey.getMM()/2-magnetDepth.getMM())
	magnet = magnet.move(xkey.getMM()/2-3*magnetDiameter.getMM()/4,ykey.getMM()/2-3*magnetDiameter.getMM()/4,0);
	magnet = magnet.union(magnet.mirrorx())
	magnet = magnet.union(magnet.mirrory())
	cube = cube.difference(magnet)
	def cutout = new Cube(cutoutx.getMM(),cutouty.getMM(),zkey.getMM()).toCSG()
	cube = cube.difference(cutout)
	cube = cube.setName("Holder")
	return cube
		.setParameter(cutoutx)
		.setParameter(cutouty)
		.setParameter(magnetDiameter)
		.setParameter(magnetDepth)
		.setRegenerate({ makeHolder()})
}

CSG makeConnector(){
	CSG dovetail = makeDoveTail()
	return dovetail.union(dovetail.rotz(180))
	.setParameter(doveTailWidth)
		.setParameter(doveTailHeight)
		.setRegenerate({makeConnector()})
}

CSG makeDoveTail() {
	def h = doveTailHeight.getMM()/2
	def w = doveTailWidth.getMM()/2
	return Extrude.points(new Vector3d(0, 0, zkey.getMM()),
                new Vector3d(-w/6,0),
                new Vector3d(-w/2,h),
                new Vector3d(w/2,h),
                new Vector3d(w/6,0)
        ).movez(-(zkey.getMM()/2));
}
	
CSG makeMagnet(){
	return new Cylinder(magnetDiameter.getMM()/2,magnetDepth.getMM()).toCSG()
}

CSG holder = makeHolder().setColor(javafx.scene.paint.Color.BLUE);
CSG conn = makeConnector()
			.setColor(javafx.scene.paint.Color.RED);

BowlerStudioController.addCsg(holder)
BowlerStudioController.addCsg(conn.movey(ykey.getMM()/2).setName("Connector"))
BowlerStudioController.addCsg(conn.movey(xkey.getMM()/2).rotz(90))
BowlerStudioController.addCsg(conn.movey(ykey.getMM()/2).rotz(180))
BowlerStudioController.addCsg(conn.movey(xkey.getMM()/2).rotz(270))
BowlerStudioController.addCsg(holder.movey(ykey.getMM()).setColor(javafx.scene.paint.Color.CYAN))
return null
