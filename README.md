# Tiketons 🎵 - Aplikasi Pemesanan Tiket Konser

**Tiketons** adalah aplikasi mobile berbasis Android yang dikembangkan menggunakan **Kotlin** dan **Jetpack Compose**. Aplikasi ini memungkinkan pengguna untuk mencari jadwal konser, memesan tiket, melakukan simulasi pembayaran, dan mengelola E-Ticket secara digital.

Aplikasi ini menggunakan **Supabase** sebagai Backend-as-a-Service (BaaS) untuk menangani autentikasi pengguna, database PostgreSQL, dan penyimpanan data transaksi secara *real-time*.

---

## 📱 Galeri Aplikasi

Berikut adalah tampilan antarmuka pengguna (User Interface) dari Tiketons:

| **Login & Register** | **Beranda & Detail** | **Transaksi & Tiket** | **Riwayat & Profil** |
| :---: | :---: | :---: | :---: |
| <img width="1080" height="2340" alt="Image" src="https://github.com/user-attachments/assets/0be45da8-689b-4f6e-af74-9773b7280775" /><br> *Halaman Login* | <img width="1080" height="2340" alt="Image" src="https://github.com/user-attachments/assets/96b3a0e0-f599-4324-8489-8a03376d65f3" /> <br> *Daftar Konser* | <img width="1080" height="2340" alt="Image" src="https://github.com/user-attachments/assets/671c2a74-7ec8-42c0-9f26-133c0cf490a7" /> <br> *Simulasi Bayar* |<img width="1080" height="2340" alt="Image" src="https://github.com/user-attachments/assets/e640e0e0-b483-460b-ae42-fe5402b00642" /> <br> *Riwayat Transaksi* |
| <img width="1080" height="2340" alt="Image" src="https://github.com/user-attachments/assets/bf42b058-8672-4737-a6cd-ad2fc22b92c6" /> <br> *Halaman Daftar* | <img width="1080" height="2340" alt="Image" src="https://github.com/user-attachments/assets/5c7d22f7-0995-4d26-8917-7d52d95857c4" /> <br> *Detail Event* | <img width="1080" height="2340" alt="Image" src="https://github.com/user-attachments/assets/f5815577-d3f5-4dbc-9cb0-55ca863e53cd" /> <br> *E-Ticket Digital* | <img width="1080" height="2340" alt="Image" src="LINK_GAMBAR_PROFILE_DISINI" /> <br> *Manajemen Profile* |

> **Catatan:** Ganti path gambar di atas (misal: `docs/img/login_screen.png`) dengan lokasi file gambar screenshot Anda yang sebenarnya.

---

## ✨ Fitur Utama

1.  **Autentikasi Pengguna**: Sistem pendaftaran (Register) dan masuk (Login) yang aman dengan validasi email menggunakan Supabase Auth.
2.  **Katalog Event**: Menampilkan daftar konser aktif, lengkap dengan poster, tanggal, lokasi, dan harga.
3.  **Pencarian**: Fitur pencarian untuk memudahkan pengguna menemukan konser favorit.
4.  **Booking System**: Pemilihan kategori tiket (VIP / Regular) dan simulasi pembayaran (Virtual Account/Transfer).
5.  **Manajemen Tiket**:
    * Menampilkan riwayat tiket yang sudah dibeli.
    * **E-Ticket Visual**: Tampilan tiket digital dengan desain sobekan kertas yang realistis.
    * **Status Otomatis**: Status tiket berubah otomatis (Aktif, Terpakai, Selesai) berdasarkan tanggal event.

---

## 🛠️ Teknologi yang Digunakan (Tech Stack)

* **Bahasa Pemrograman**: Kotlin
* **UI Toolkit**: Jetpack Compose (Material Design 3)
* **Arsitektur**: MVVM (Model-View-ViewModel)
* **Backend & Database**: Supabase (PostgreSQL)
* **Networking**: Ktor / Supabase Client
* **Image Loading**: Coil
* **Navigation**: Jetpack Navigation Compose

---

## 🗄️ Struktur Database

Aplikasi ini menggunakan 4 tabel utama yang saling berelasi:

1.  **`profiles`**: Data pengguna (email, nama lengkap, username).
2.  **`events`**: Data konser (nama, deskripsi, harga, tanggal, lokasi).
3.  **`transactions`**: Riwayat pesanan dan status pembayaran.
4.  **`tickets`**: Data tiket valid yang memiliki kode unik dan status penggunaan.
