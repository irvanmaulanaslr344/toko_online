# Toko Online API

REST API untuk e-commerce toko online.  
**Base URL**: `https://toko_online.railway.app/api`  
**Local**: `http://localhost:8080/api`

---

## AUTHENTICATION

Semua request selain public endpoint wajib kirim header:

Token didapat dari login/register.

---

## ENDPOINTS

### AUTH
| Method | Endpoint | Body | Keterangan |
|--------|----------|------|------------|
| POST | /auth/register | `{email, password, name, role:"BUYER"\|"SELLER", phone}` | Daftar akun |
| POST | /auth/login | `{email, password}` | Login, dapat token |

---

### PUBLIC (Tanpa Auth)
| Method | Endpoint | Params | Keterangan |
|--------|----------|--------|------------|
| GET | /products | `?search=&categoryId=&minPrice=&maxPrice=&page=0&size=20` | List produk |
| GET | /products/{id} | - | Detail produk |
| GET | /categories | - | List kategori |

---

### BUYER (Perlu Token BUYER)
| Method | Endpoint | Body/Params | Keterangan |
|--------|----------|-------------|------------|
| GET | /cart | - | Lihat keranjang |
| POST | /cart | `{productId, quantity}` | Tambah ke keranjang |
| PUT | /cart/{id} | `?quantity=3` | Update jumlah |
| DELETE | /cart/{id} | - | Hapus dari keranjang |
| POST | /orders/checkout | `{addressId}` | Buat pesanan dari cart |
| GET | /orders/my | `?page=0&size=10` | Riwayat pesanan |
| GET | /orders/{id} | - | Detail pesanan |
| GET | /addresses | - | List alamat |
| POST | /addresses | `{label, fullAddress, city, postalCode, phone}` | Tambah alamat |
| PUT | /addresses/{id} | `{...}` | Update alamat |
| DELETE | /addresses/{id} | - | Hapus alamat |
| POST | /reviews/order-items/{orderItemId} | `{rating:1-5, comment}` | Review produk |

---

### SELLER (Perlu Token SELLER)
| Method | Endpoint | Body/Params | Keterangan |
|--------|----------|-------------|------------|
| GET | /seller/dashboard | - | Statistik toko |
| GET | /seller/products | `?page=0&size=20` | Produk sendiri |
| POST | /seller/products | `{name, description, price, stock, categoryId}` | Tambah produk |
| PUT | /seller/products/{id} | `{...}` | Edit produk |
| DELETE | /seller/products/{id} | - | Hapus produk |
| POST | /seller/discounts/products/{productId} | `{discountType:"PERCENTAGE"\|"FIXED", discountValue, startDate, endDate}` | Tambah diskon |
| GET | /seller/discounts | - | List diskon |
| PUT | /seller/discounts/{id} | `{...}` | Edit diskon |
| DELETE | /seller/discounts/{id} | - | Hapus diskon |
| GET | /seller/orders | `?page=0&size=10` | Pesanan masuk |
| GET | /seller/orders/{id} | - | Detail pesanan |
| PUT | /seller/orders/{id}/status | `{status:"PROCESSED"\|"SHIPPED"\|"DELIVERED"}` | Update status |

---

## RESPONSE FORMAT

Semua response pakai format:
```json
{
    "success": true,
    "message": "Berhasil",
    "data": {},
    "timestamp": "2026-07-21T..."
}

{
    "success": false,
    "message": "Pesan error",
    "timestamp": "2026-07-21T..."
}

PENDING → Pesanan baru

PROCESSED → Diproses penjual

SHIPPED → Dikirim

DELIVERED → Sampai

CANCELLED → Dibatalkan

Java 17

Spring Boot 3.2

MySQL

JWT Auth
