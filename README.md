# Jar Native Loader

Doc temporaire rapide :

Exemple:

Si le chemin d'accès à la lib en utilisant un Raspberry Pi:

/jni/chacon/emission/arm/libbidule.so


		final NativeLoader nativeLoader = PropertiesJarNativeLoader.getInstance();
		nativeLoader.loadLibrary("/jni/chacon/emission/{os.arch}/bidule");
		
os.arch est une propriete systeme Java et le nom est résoud avec System.mapLibraryName
