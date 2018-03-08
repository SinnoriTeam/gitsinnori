package kr.pe.sinnori.common.io;

import java.nio.ByteOrder;
import java.util.ArrayDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;

public class DataPacketBufferPool implements DataPacketBufferPoolIF {
	private Logger log = LoggerFactory.getLogger(DataPacketBufferPool.class);

	private final Object monitor = new Object();

	private ArrayDeque<WrapBuffer> dataPacketBufferQueue = null;
	/*
	 * private Set<Integer> queueOutWrapBufferHashcodeSet = new HashSet<Integer>();
	 * private Set<Integer> allWrapBufferHashcodeSet = new HashSet<Integer>();
	 */

	private boolean isDirect;
	private ByteOrder dataPacketBufferByteOrder = null;
	private int dataPacketBufferSize;
	private int dataPacketBufferPoolSize;

	public DataPacketBufferPool(boolean isDirect, ByteOrder dataPacketBufferByteOrder, int dataPacketBufferSize,
			int dataPacketBufferPoolSize) {
		if (null == dataPacketBufferByteOrder) {
			throw new IllegalArgumentException("the parameter dataPacketBufferByteOrder is null");
		}

		if (dataPacketBufferSize <= 0) {
			String errorMessage = String.format("the parameter dataPacketBufferSize[%d] is less than or equal to zero",
					dataPacketBufferSize);
			throw new IllegalArgumentException(errorMessage);
		}

		if (dataPacketBufferPoolSize <= 0) {
			String errorMessage = String.format(
					"the parameter dataPacketBufferPoolSize[%d] is less than or equal to zero",
					dataPacketBufferPoolSize);
			throw new IllegalArgumentException(errorMessage);
		}

		this.isDirect = isDirect;
		this.dataPacketBufferByteOrder = dataPacketBufferByteOrder;
		this.dataPacketBufferSize = dataPacketBufferSize;
		this.dataPacketBufferPoolSize = dataPacketBufferPoolSize;

		dataPacketBufferQueue = new ArrayDeque<WrapBuffer>(dataPacketBufferPoolSize);

		try {

			for (int i = 0; i < dataPacketBufferPoolSize; i++) {
				WrapBuffer dataPacketBuffer = new WrapBuffer(isDirect, dataPacketBufferSize, dataPacketBufferByteOrder);
				dataPacketBuffer.setPoolBuffer(true);
				dataPacketBufferQueue.add(dataPacketBuffer);
				// allWrapBufferHashcodeSet.add(dataPacketBuffer.hashCode());
			}

			// log.info("the wrap buffer hashcode set={}",
			// allWrapBufferHashcodeSet.toString());
		} catch (OutOfMemoryError e) {
			String errorMessage = "OutOfMemoryError";
			log.error(errorMessage, e);
			System.exit(1);
		}
	}

	@Override
	public WrapBuffer pollDataPacketBuffer() throws NoMoreDataPacketBufferException {
		synchronized (monitor) {
			WrapBuffer dataPacketBuffer = dataPacketBufferQueue.poll();
			if (null == dataPacketBuffer) {
				String errorMessage = "no more wrap buffer in the wrap buffer polling queue";
				throw new NoMoreDataPacketBufferException(errorMessage);
			}

			dataPacketBuffer.queueOut();

			// queueOutWrapBufferHashcodeSet.add(dataPacketBuffer.hashCode());

			// FIXME!, 테스트후 삭제 필요
			/*
			 * { String infoMessage = String.
			 * format("the WrapBuffer[%d] is removed from the wrap buffer polling queue",
			 * dataPacketBuffer.hashCode()); log.info(infoMessage, new
			 * Throwable(infoMessage)); }
			 */

			return dataPacketBuffer;
		}
	}

	@Override
	public void putDataPacketBuffer(WrapBuffer dataPacketBuffer) {
		if (null == dataPacketBuffer) {
			return;
		}

		/**
		 * 2번 연속 반환 막기
		 */
		synchronized (monitor) {
			if (! dataPacketBuffer.isPoolBuffer()) {
				String errorMessage = String.format("the parameter dataPacketBuffer[%d] is not a pool wrap buffer",
						dataPacketBuffer.hashCode());
				log.warn(errorMessage, new Throwable(errorMessage));
				throw new IllegalArgumentException(errorMessage);
			}

			if (dataPacketBuffer.isInQueue()) {
				String errorMessage = String.format(
						"the parameter dataPacketBuffer[%d] was added to the wrap buffer polling queue",
						dataPacketBuffer.hashCode());
				log.warn(errorMessage, new Throwable(errorMessage));
				throw new IllegalArgumentException(errorMessage);
			}

			dataPacketBuffer.queueIn();
			dataPacketBufferQueue.add(dataPacketBuffer);

			// FIXME!, 테스트후 삭제 필요
			/*
			 * { String infoMessage = String.
			 * format("the parameter dataPacketBuffer[%d] is added to the wrap buffer polling queue"
			 * , dataPacketBuffer.hashCode()); log.info(infoMessage, new
			 * Throwable(infoMessage)); }
			 */
		}

	}

	@Override
	public final int getDataPacketBufferSize() {
		return dataPacketBufferSize;
	}

	public String getQueueState() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("dataPacketBufferPoolSize=[");
		strBuilder.append(dataPacketBufferPoolSize);
		strBuilder.append("], remaing size=[");
		strBuilder.append(dataPacketBufferQueue.size());
		strBuilder.append("]");
		return strBuilder.toString();
	}

	@Override
	public final ByteOrder getByteOrder() {
		return dataPacketBufferByteOrder;
	}

	public final int getDataPacketBufferPoolSize() {
		return dataPacketBufferPoolSize;
	}

	public boolean isDirect() {
		return isDirect;
	}
}