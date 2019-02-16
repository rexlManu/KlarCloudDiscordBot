package de.klarcloudservice.internal.utility;

public class SupportInfo
{
  private int channelID;
  private int userID;
  private int supID;
  private boolean work;

  public SupportInfo(final int channelID, final int userID) {
    this.work = false;
    this.channelID = channelID;
    this.userID = userID;
  }

  public int getChannelID() {
    return this.channelID;
  }

  public int getUserID() {
    return this.userID;
  }

  public int getSupID() {
    return this.supID;
  }

  public boolean isWork() {
    return this.work;
  }

  public void setChannelID(final int channelID) {
    this.channelID = channelID;
  }

  public void setUserID(final int userID) {
    this.userID = userID;
  }

  public void setSupID(final int supID) {
    this.supID = supID;
  }

  public void setWork(final boolean work) {
    this.work = work;
  }
}
