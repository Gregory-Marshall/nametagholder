CSG makeCube(){
	//Set up som parameters to use
	xkey 		= new LengthParameter("X dimention",120,[120.0,1.0])
	ykey 		= new LengthParameter("Y dimention",60,[130.0,2.0])
	zkey 		= new LengthParameter("Z dimention",5,[140.0,3.0])
	def cube = new Cube(xkey,ykey,zkey).toCSG()	
	def doveTailWidth = new LengthParameter("Dove Tail Width",5,[140.0,3.0])
	def doveTailHeight = new LengthParameter("Dove Tail Height",5,[140.0,3.0])
	return cube
		.setParameter(doveTailWidth)
		.setParameter(doveTailHeight)
		.setRegenerate({ makeCube()})
}	
return makeCube();
