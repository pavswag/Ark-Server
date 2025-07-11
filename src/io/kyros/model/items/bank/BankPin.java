package io.kyros.model.items.bank;

import java.util.concurrent.TimeUnit;

import io.kyros.model.entity.player.Player;

/**
 * 
 * @author Jason MacKeigan
 * @date July 10th, 2014, 2:41:21 PM
 */
public class BankPin {

	private final Player player;
	private String pin = "";
	private boolean locked = true;
	private long cancellationDelay = -1;
	private boolean appendingCancellation;
	private int attempts;
	private PinState pinState;

	private long unlockDelay;

	public BankPin(Player player) {
		this.player = player;
	}

	public enum PinState {
		CREATE_NEW, UNLOCK, CANCEL_PIN, CANCEL_REQUEST
	}

	public void openInterface() {
		if (getPin().length() <= 0) {
			open(1);
		} else if (!getPin().isEmpty() && !isAppendingCancellation()) {
			open(3);
		} else if (!getPin().isEmpty() && isAppendingCancellation()) {
			open(4);
		}
	}

	public void open(int state) {
		player.getPA().sendFrame126("", 59507);
		switch (state) {
		case 1:
			pinState = PinState.CREATE_NEW;
			player.getPA().sendFrame126("You do not have a pin set.", 59503);
			player.getPA().sendFrame126("Choose any 4-8 character combination.", 59504);
			player.getPA().sendFrame126("Make sure caps lock isn't enabled.", 59505);
			player.getPA().sendFrame126("Press enter to continue", 59506);
			break;
		case 2:
			pinState = PinState.UNLOCK;
			player.getPA().sendFrame126("You currently have a pin set.", 59503);
			player.getPA().sendFrame126("Type in your 4-8 character combination.", 59504);
			player.getPA().sendFrame126("Hit enter after you've typed your pin.", 59505);
			player.getPA().sendFrame126("Press the button to continue", 59506);
			break;
		case 3:
			pinState = PinState.CANCEL_PIN;
			player.getPA().sendFrame126("If you wish to cancel your pin, ", 59503);
			player.getPA().sendFrame126("click the button below. If not", 59504);
			player.getPA().sendFrame126("click the x button in the corner.", 59505);
			player.getPA().sendFrame126("Press the button to continue", 59506);
			break;
		case 4:
			pinState = PinState.CANCEL_REQUEST;
			player.getPA().sendFrame126("Your current pin cancellation is", 59503);
			player.getPA().sendFrame126("pending. Press continue to cancel", 59504);
			player.getPA().sendFrame126("this and keep your bank pin.", 59505);
			player.getPA().sendFrame126("Press the button to continue", 59506);
			break;
		}
		player.getDH().sendStatement("Please enter your bank pin.");
		player.getPA().showInterface(59500);
	}

	public void create(String pin) {
		if (this.pin.length() > 0) {
			player.sendMessage("@dre@You already have a pin, you cannot create another one.");
			return;
		}
		if (pin.length() < 4) {
			player.sendMessage("@dre@Your pin must be at least 4 characters in length.");
			return;
		}
		if (pin.length() > 8) {
			player.sendMessage("@dre@Your pin cannot be longer than 8 characters in length.");
			return;
		}
		if (!pin.matches("[A-Za-z0-9]+")) {
			player.sendMessage("@dre@Your bank pin contains illegal characters. Pins can only contain numbers,");
			player.sendMessage("@dre@and uppercase, and lowercase case letters.");
			return;
		}
		if (pin.contains(" ")) {
			player.sendMessage("@dre@Your bank pin contains 1 or more spaces, bank pins cannot contain spaces.");
			return;
		}
		if (pin.equalsIgnoreCase(player.getLoginName())) {
			player.sendMessage("@dre@Your bank pin cannot match your username.");
			return;
		}
		player.sendMessage("@dre@You have successfully created a bank pin. We urge you to keep this combination");
		player.sendMessage("@dre@to yourself as sharing it may jeopardize the items you have in your bank.");
		SetFirstBankPinReward.giveRewardOnFirstBankPin(player);
		this.pin = pin;
		this.locked = true;
		this.attempts = 0;
	}

	public void unlock(String pin) {
		if (!locked) {
			return;
		}
		if (System.currentTimeMillis() < this.unlockDelay) {
			int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(unlockDelay - System.currentTimeMillis());
			int seconds = (int) TimeUnit.MILLISECONDS.toSeconds((unlockDelay - System.currentTimeMillis()) - TimeUnit.MINUTES.toMillis(minutes));
			player.sendMessage("@dre@You must wait " + minutes + " minute(s) and " + seconds + " second(s) to try unlocking the pin.");
			return;
		}
		if (!this.pin.equals(pin)) {
			player.sendMessage("@dre@The pin you entered does not match your current bank pin, please try again.");
			attempts++;
			if (attempts == 3) {
				unlockDelay = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30);
				player.sendMessage("@dre@You typed an incorrect pin too many time, you must wait 30 seconds.");
			}
			if (attempts == 4) {
				unlockDelay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2);
				player.sendMessage("@dre@You typed an incorrect pin too many time, you must wait 2 minutes.");
			}
			if (attempts >= 5) {
				unlockDelay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
				player.sendMessage("@dre@You typed an incorrect pin too many time, you must wait 5 minutes.");
			}
		} else {
			player.setRequiresPinUnlock(false);
			player.getPA().closeAllWindows();
			attempts = 0;
			locked = false;
			player.sendMessage("You have successfully entered your " + this.pin.length() + " character pin");
			update();
		}
	}

	public void cancel(String pin) {
		if (!this.pin.equals(pin)) {
			player.sendMessage("@dre@The pin you entered does not match your current bank pin, please try again.");
			this.attempts++;
			return;
		}
		if (this.pinState == PinState.CANCEL_PIN) {
			this.setAppendingCancellation(true);
			this.cancellationDelay = System.currentTimeMillis();
			this.player.sendMessage("@dre@Your pin is currently pending cancellation and will expire in 3 days.");
			this.player.sendMessage("@dre@If you want to cancel this, simply open up this interface again.");
		} else if (this.pinState == PinState.CANCEL_REQUEST) {
			this.setAppendingCancellation(false);
			this.cancellationDelay = -1;
			this.player.sendMessage("@dre@Your pin is no longer going to be cancelled.");
		}
	}

	public void update() {
		if (this.appendingCancellation) {
			if (System.currentTimeMillis() - this.cancellationDelay > (1000 * 60 * 60 * 24 * 3)) {
				this.pin = "";
				this.cancellationDelay = -1;
				this.attempts = 0;
				this.locked = false;
				this.appendingCancellation = false;
				player.sendMessage("@dre@Your pin has successfully been reset. If you wish to set another pin, you may do so.");
			} else
				player.sendMessage("@dre@Your pin is still pending its cancellation and will be reset 3 days after the initial date.");
		}
	}

	public boolean hasBankPin() {
		return pin != null && pin.length() != 0;
	}

	public boolean requiresUnlock() {
		return hasBankPin() && locked && player.isRequiresPinUnlock();
	}

	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public long getCancellationDelay() {
		return cancellationDelay;
	}

	public void setCancellationDelay(long cancellationDelay) {
		this.cancellationDelay = cancellationDelay;
	}

	public boolean isAppendingCancellation() {
		return appendingCancellation;
	}

	public void setAppendingCancellation(boolean appendingCancellation) {
		this.appendingCancellation = appendingCancellation;
	}

	public PinState getPinState() {
		return pinState;
	}

	public void setPinState(PinState pinState) {
		this.pinState = pinState;
	}

	public long getUnlockDelay() {
		return unlockDelay;
	}

	public void setUnlockDelay(long delay) {
		this.unlockDelay = delay;
	}

}
