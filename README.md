# JCSG-MeshExtensions
Additional functionality for producing high-quality meshes from JCSG objects. The mesh optimization is performed with the cross-platform meshing software [ProMesh](http://promesh3d.com/) which is available as ug4 plugin.

<img src="https://raw.githubusercontent.com/miho/JCSG-MeshExtensions/master/res/img/optimize.jpg" alt="optimization" width="450">

## Sample Code
```java
// we use cube and sphere as base geometries
CSG cube = new Cube(2).toCSG();
CSG sphere = new Sphere(1.25).toCSG();

// compute difference between cube and sphere
CSG cubeMinusSphere = cube.difference(sphere);

// create a copy of cube-sphere that shall be optimized
CSG optimized       = cubeMinusSphere.
        transformed(Transform.unity().translateX(3));

// perform the optimization
CSG all = MeshTools.optimize(
        optimized, // csg object to optimize
        1e-6,      // tolerance
        1e-4,      // max tolerance
        0.25,      // min edge length
        1.5        // max edge length
);

// save optimized mesh as "all.stl"
Files.write(Paths.get("all.stl"), all.toStlString().getBytes());
```
