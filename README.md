# Tiketons 🎵 - Aplikasi Pemesanan Tiket Konser

**Tiketons** adalah aplikasi mobile berbasis Android yang dikembangkan menggunakan **Kotlin** dan **Jetpack Compose**. Aplikasi ini memungkinkan pengguna untuk mencari jadwal konser, memesan tiket, melakukan simulasi pembayaran, dan mengelola E-Ticket secara digital.

Aplikasi ini menggunakan **Supabase** sebagai Backend-as-a-Service (BaaS) untuk menangani autentikasi pengguna, database PostgreSQL, dan penyimpanan data transaksi secara *real-time*.

---

## 📱 Galeri Aplikasi

Berikut adalah tampilan antarmuka pengguna (User Interface) dari Tiketons:

| **Login & Register** | **Beranda & Detail** | **Transaksi & Tiket** |
| :---: | :---: | :---: |
| <img width="1080" height="2340" alt="Image" src="https://github.com/user-attachments/assets/bf42b058-8672-4737-a6cd-ad2fc22b92c6" /> <br> *Halaman Login* | <img width="1080" height="2340" alt="Image" src="https://github.com/user-attachments/assets/0be45da8-689b-4f6e-af74-9773b7280775" /> <br> *Halaman Sign Up* | <img src="docs/img/home_screen.png" width="200" /> <br> *Daftar Konser* | <img src="docs/img/payment_screen.png" width="200" /> <br> *Simulasi Bayar* |
| <img src="docs/img/register_screen.png" width="200" /> <br> *Halaman Daftar* | <img src="docs/img/detail_screen.png" width="200" /> <br> *Detail Event* | <img src="docs/img/eticket_screen.png" width="200" /> <br> *E-Ticket Digital* |

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
