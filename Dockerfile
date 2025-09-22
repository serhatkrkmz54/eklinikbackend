# 1. Malzeme: Temiz bir mutfak ve Java 17 fırını al.
FROM openjdk:17-slim

# 2. Hazırlık: Çalışma tezgahı olarak /app adında bir alan belirle.
WORKDIR /app

# 3. Ana Malzemeyi Ekleme: Projemizin paketlenmiş hali olan .jar dosyasını tezgaha kopyala ve adını app.jar yap.
# Maven kullanıyorsanız 'target/*.jar', Gradle kullanıyorsanız 'build/libs/*.jar' olmalı.
COPY target/*.jar app.jar

# 4. Pişirme Komutu: Fırını çalıştır. Yani "java -jar app.jar" komutuyla uygulamayı başlat.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
