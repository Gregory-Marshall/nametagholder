import java.awt.Color

import eu.mihosoft.vrl.v3d.*
import eu.mihosoft.vrl.v3d.parametrics.*
xkey 		= new LengthParameter("X dimention",120,[120.0,1.0])
ykey 		= new LengthParameter("Y dimention",30,[130.0,2.0])
zkey 		= new LengthParameter("Z dimention",2,[140.0,3.0])
doveTailWidth = new LengthParameter("Dove Tail Width",5,[140.0,3.0])
doveTailHeight = new LengthParameter("Dove Tail Height",5,[140.0,3.0])
CSG makeHolder(){
	def cube = new Cube(xkey,ykey,zkey).toCSG()
	def dovetail = makeDoveTail()
	cube = cube.difference(dovetail.movey(-ykey.getMM()/2))
	cube = cube.difference(dovetail.movey(-ykey.getMM()/2).rotz(180))
	cube = cube.difference(dovetail.movey(-xkey.getMM()/2).rotz(90))
	cube = cube.difference(dovetail.movey(-xkey.getMM()/2).rotz(270))
	def cutout = new Cube(45,13,zkey.getMM()).toCSG()
	cube = cube.difference(cutout)
	cube = cube.setName("Holder")
	return cube
		.setParameter(doveTailWidth)
		.setParameter(doveTailHeight)
		.setRegenerate({ makeHolder()})
}

CSG makeConnector(){
	CSG dovetail = makeDoveTail()
	return dovetail.union(dovetail.rotz(180))
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
