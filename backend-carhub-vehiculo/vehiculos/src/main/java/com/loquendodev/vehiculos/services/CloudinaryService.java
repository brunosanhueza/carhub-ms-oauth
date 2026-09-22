// package com.loquendodev.vehiculos.services;

// import java.io.IOException;
// import java.util.Map;

// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import com.cloudinary.Cloudinary;
// import com.cloudinary.utils.ObjectUtils;

// @Service
// public class CloudinaryService{
//     private final Cloudinary cloudinary;


//     public CloudinaryService(Cloudinary cloudinary){
//         this.cloudinary = cloudinary;
//     }


//     public String subirImagen(MultipartFile archivo) throws IOException{
//         Map uploadResult = cloudinary.uploader().upload(archivo.getBytes(), ObjectUtils.emptyMap());
//         return uploadResult.get("secure_url").toString();
//     }
// }