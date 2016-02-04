package com.sagarius.goddess.server.message;

public abstract class MessageGateway {
	public static class MessageResponse {
		private String status;
		private boolean hasSucceeded;

		public MessageResponse() {
		}

		public MessageResponse(String status, boolean hasSucceeded) {
			this.status = status;
			this.hasSucceeded = hasSucceeded;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public boolean isHasSucceeded() {
			return hasSucceeded;
		}

		public void setHasSucceeded(boolean hasSucceeded) {
			this.hasSucceeded = hasSucceeded;
		}

		@Override
		public String toString() {
			return "MessageResponse [status=" + status + ", hasSucceeded="
					+ hasSucceeded + "]";
		}
	}

	public static enum Priority {
		DEFAULT, LOW, NORMAL, HIGH, ECONOMY;
	}

	public abstract MessageResponse send(String message, String phoneNumbers,
			String senderId, Priority priority);

	public MessageResponse send(String message, String phoneNumbers) {
		return send(message, phoneNumbers, null, Priority.HIGH);
	}

	public MessageResponse send(String message, String phoneNumbers,
			String senderId) {
		return send(message, phoneNumbers, senderId, Priority.HIGH);
	}

	public abstract String[] getStatus(String[] ids);

}
