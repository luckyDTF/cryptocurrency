package com.itranswarp.bitcoin.struct;

import java.io.IOException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itranswarp.bitcoin.io.BitcoinInput;
import com.itranswarp.bitcoin.io.BitcoinOutput;
import com.itranswarp.bitcoin.util.HashUtils;
import com.itranswarp.cryptocurrency.common.HashSerializer;
import com.itranswarp.cryptocurrency.common.LockTimeSerializer;

public class Transaction {

	public int version; // int32_t, transaction data format version (signed)
	public TxIn[] tx_ins; // a list of 1 or more transaction inputs or sources
							// for coins
	public TxOut[] tx_outs; // a list of 1 or more transaction outputs or
							// destinations
	// for coins
	public long lock_time; // uint32_t, the block number or timestamp at which
							// this transaction is unlocked:
	// 0 Not locked
	// < 500000000 Block number at which this transaction is unlocked
	// >= 500000000 UNIX timestamp at which this transaction is unlocked
	// If all TxIn inputs have final (0xffffffff) sequence numbers then
	// lock_time is irrelevant. Otherwise, the transaction may not be added to a
	// block until after lock_time (see NLockTime).

	public Transaction(BitcoinInput input) throws IOException {
		this.version = input.readInt();
		long tx_in_count = input.readVarInt();
		this.tx_ins = new TxIn[(int) tx_in_count];
		for (int i = 0; i < this.tx_ins.length; i++) {
			this.tx_ins[i] = new TxIn(input);
		}
		long tx_out_count = input.readVarInt();
		this.tx_outs = new TxOut[(int) tx_out_count];
		for (int i = 0; i < this.tx_outs.length; i++) {
			this.tx_outs[i] = new TxOut(input);
		}
		this.lock_time = input.readUnsignedInt();
	}

	@JsonSerialize(using = HashSerializer.class)
	public byte[] getHash() {
		return HashUtils.doubleSha256(this.toByteArray());
	}

	public byte[] calculateHash() {
		return HashUtils.doubleSha256(this.toByteArray());
	}

	public byte[] toByteArray() {
		BitcoinOutput buffer = new BitcoinOutput();
		buffer.writeInt(this.version).writeVarInt(this.tx_ins.length);
		for (int i = 0; i < this.tx_ins.length; i++) {
			buffer.write(tx_ins[i].toByteArray());
		}
		buffer.writeVarInt(this.tx_outs.length);
		for (int i = 0; i < this.tx_outs.length; i++) {
			buffer.write(tx_outs[i].toByteArray());
		}
		buffer.writeUnsignedInt(lock_time);
		return buffer.toByteArray();
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public long getTxInCount() {
		return this.tx_ins.length;
	}

	public TxIn[] getTx_ins() {
		return tx_ins;
	}

	public void setTx_ins(TxIn[] tx_ins) {
		this.tx_ins = tx_ins;
	}

	public long getTxOutCount() {
		return this.tx_outs.length;
	}

	public TxOut[] getTx_outs() {
		return tx_outs;
	}

	public void setTx_outs(TxOut[] tx_outs) {
		this.tx_outs = tx_outs;
	}

	@JsonSerialize(using = LockTimeSerializer.class)
	public long getLockTime() {
		return lock_time;
	}

	public void setLockTime(long lock_time) {
		this.lock_time = lock_time;
	}

}