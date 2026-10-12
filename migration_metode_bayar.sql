-- =====================================================================
-- BOSS LAUNDRY - Migrasi tambahan: metode pembayaran pada transaksi
-- =====================================================================
-- Menambahkan kolom metode pembayaran, uang bayar, dan kembalian pada
-- tabel transaksi_laundry. Bersifat NON-DESTRUKTIF dan idempoten
-- (ADD COLUMN IF NOT EXISTS), aman dijalankan berulang.
--
-- Rollback (bila diperlukan):
--   ALTER TABLE transaksi_laundry DROP COLUMN IF EXISTS metode_bayar;
--   ALTER TABLE transaksi_laundry DROP COLUMN IF EXISTS bayar;
--   ALTER TABLE transaksi_laundry DROP COLUMN IF EXISTS kembalian;
-- =====================================================================

ALTER TABLE transaksi_laundry
    ADD COLUMN IF NOT EXISTS metode_bayar varchar(20) DEFAULT 'Tunai';

ALTER TABLE transaksi_laundry
    ADD COLUMN IF NOT EXISTS bayar double precision;

ALTER TABLE transaksi_laundry
    ADD COLUMN IF NOT EXISTS kembalian double precision;

-- Isi nilai default untuk data lama yang belum memiliki metode pembayaran.
UPDATE transaksi_laundry SET metode_bayar = 'Tunai' WHERE metode_bayar IS NULL;
