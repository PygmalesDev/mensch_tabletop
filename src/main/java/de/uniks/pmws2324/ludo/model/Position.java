package de.uniks.pmws2324.ludo.model;

import java.util.Objects;
import java.beans.PropertyChangeSupport;

import static de.uniks.pmws2324.ludo.Constants.*;

public class Position
{
   public static final String PROPERTY_X = "x";
   public static final String PROPERTY_Y = "y";
   public static final String PROPERTY_GLOBAL_STATE = "globalState";
   public static final String PROPERTY_LOCAL_STATE = "localState";
   public static final String PROPERTY_CONE = "cone";
   public static final String PROPERTY_PLAYER = "player";
   public static final String PROPERTY_BASE_PLAYER = "basePlayer";
   private int x;
   private int y;
   private int globalState;
   private int localState;
   private Cone cone;
   protected PropertyChangeSupport listeners;
   private Player player;
   private Player basePlayer;

   // --------------------- CUSTOM METHODS ---------------------
   public boolean hasCone() {
      return !Objects.isNull(this.cone);
   }

   public Position clone() {
      return new Position()
              .setX(x).setY(y)
              .setGlobalState(globalState)
              .setLocalState(localState);
   }

   public boolean isInRadius(int testX, int textY) {
      return     testX >= x-CONE_OFFSET_X/2 && testX <= x+CONE_OFFSET_X/2
              && textY >= y-CONE_OFFSET_Y/2 && textY <= y+CONE_OFFSET_Y/2;
   }

   // ------------------- GENERATED METHODS --------------------
   public int getX()
   {
      return this.x;
   }

   public Position setX(int value)
   {
      if (value == this.x)
      {
         return this;
      }

      final int oldValue = this.x;
      this.x = value;
      this.firePropertyChange(PROPERTY_X, oldValue, value);
      return this;
   }

   public int getY()
   {
      return this.y;
   }

   public Position setY(int value)
   {
      if (value == this.y)
      {
         return this;
      }

      final int oldValue = this.y;
      this.y = value;
      this.firePropertyChange(PROPERTY_Y, oldValue, value);
      return this;
   }

   public int getGlobalState()
   {
      return this.globalState;
   }

   public Position setGlobalState(int value)
   {
      if (value == this.globalState)
      {
         return this;
      }

      final int oldValue = this.globalState;
      this.globalState = value;
      this.firePropertyChange(PROPERTY_GLOBAL_STATE, oldValue, value);
      return this;
   }

   public int getLocalState()
   {
      return this.localState;
   }

   public Position setLocalState(int value)
   {
      if (value == this.localState)
      {
         return this;
      }

      final int oldValue = this.localState;
      this.localState = value;
      this.firePropertyChange(PROPERTY_LOCAL_STATE, oldValue, value);
      return this;
   }

   public Cone getCone()
   {
      return this.cone;
   }

   public Position setCone(Cone value)
   {
      if (this.cone == value)
      {
         return this;
      }

      final Cone oldValue = this.cone;
      if (this.cone != null)
      {
         this.cone = null;
         oldValue.setPosition(null);
      }
      this.cone = value;
      if (value != null)
      {
         value.setPosition(this);
      }
      this.firePropertyChange(PROPERTY_CONE, oldValue, value);
      return this;
   }

   public Player getPlayer()
   {
      return this.player;
   }

   public Position setPlayer(Player value)
   {
      if (this.player == value)
      {
         return this;
      }

      final Player oldValue = this.player;
      if (this.player != null)
      {
         this.player = null;
         oldValue.setStartingPosition(null);
      }
      this.player = value;
      if (value != null)
      {
         value.setStartingPosition(this);
      }
      this.firePropertyChange(PROPERTY_PLAYER, oldValue, value);
      return this;
   }

   public Player getBasePlayer()
   {
      return this.basePlayer;
   }

   public Position setBasePlayer(Player value)
   {
      if (this.basePlayer == value)
      {
         return this;
      }

      final Player oldValue = this.basePlayer;
      if (this.basePlayer != null)
      {
         this.basePlayer = null;
         oldValue.withoutBasePositions(this);
      }
      this.basePlayer = value;
      if (value != null)
      {
         value.withBasePositions(this);
      }
      this.firePropertyChange(PROPERTY_BASE_PLAYER, oldValue, value);
      return this;
   }

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public PropertyChangeSupport listeners()
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      return this.listeners;
   }

   public void removeYou()
   {
      this.setCone(null);
      this.setPlayer(null);
      this.setBasePlayer(null);
   }
}
