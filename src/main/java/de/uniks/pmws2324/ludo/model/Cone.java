package de.uniks.pmws2324.ludo.model;
import javafx.scene.image.Image;

import java.beans.PropertyChangeSupport;
import java.util.Objects;

import static de.uniks.pmws2324.ludo.Constants.*;

public class Cone
{
   public static final String PROPERTY_POSITION = "position";
   public static final String PROPERTY_PLAYER = "player";
   public static final String PROPERTY_COLOR = "color";
   public static final String PROPERTY_MOVING_DIRECTION = "movingDirection";
   public static final String PROPERTY_VISIBLE = "visible";
   public static final String PROPERTY_MOVABLE = "movable";
   public static final String PROPERTY_READY_FOR_FINISHING = "readyForFinishing";
   private Position position;
   private Player player;
   protected PropertyChangeSupport listeners;
   private String color;
   private String movingDirection;
   private boolean visible;
   private Image imgNormal;
   private Image imgSelected;
   private Image imgCurrent;
   private boolean movable;
   private boolean readyForFinishing;

   // --------------------- CUSTOM METHODS ---------------------

   public Cone loadImageNormal() {
      this.imgNormal = new Image(CONE_IMG + this.color + ".png");
      return this;
   }

   public Cone loadImageSelected() {
      this.imgSelected = new Image(CONE_IMG_SELECTED + this.color + ".png");
      return this;
   }

   public Image getCurrentImage() {
      return this.imgCurrent;
   }

   public Cone setCurrentImage(String variant) {
      if (variant.equals("NORMAL"))
         this.imgCurrent = this.imgNormal;
      if (variant.equals("SELECTED"))
         this.imgCurrent = this.imgSelected;
      return this;
   }

   // ------------------- GENERATED METHODS --------------------
   public boolean isReadyForFinishing()
   {
      return this.readyForFinishing;
   }

   public Cone setReadyForFinishing(boolean value)
   {
      if (value == this.readyForFinishing)
      {
         return this;
      }

      final boolean oldValue = this.readyForFinishing;
      this.readyForFinishing = value;
      this.firePropertyChange(PROPERTY_READY_FOR_FINISHING, oldValue, value);
      return this;
   }

   public boolean isMovable()
   {
      return this.movable;
   }

   public Cone setMovable(boolean value)
   {
      if (value == this.movable)
      {
         return this;
      }

      final boolean oldValue = this.movable;
      this.movable = value;
      this.firePropertyChange(PROPERTY_MOVABLE, oldValue, value);
      return this;
   }

   public Position getPosition()
   {
      return this.position;
   }

   public Cone setPosition(Position value)
   {
      if (this.position == value)
      {
         return this;
      }

      final Position oldValue = this.position;
      if (this.position != null)
      {
         this.position = null;
         oldValue.setCone(null);
      }
      this.position = value;
      if (value != null)
      {
         value.setCone(this);
      }
      this.firePropertyChange(PROPERTY_POSITION, oldValue, value);
      return this;
   }

   public Player getPlayer()
   {
      return this.player;
   }

   public Cone setPlayer(Player value)
   {
      if (this.player == value)
      {
         return this;
      }

      final Player oldValue = this.player;
      if (this.player != null)
      {
         this.player = null;
         oldValue.withoutCones(this);
      }
      this.player = value;
      if (value != null)
      {
         value.withCones(this);
      }
      this.firePropertyChange(PROPERTY_PLAYER, oldValue, value);
      return this;
   }

   public String getColor()
   {
      return this.color;
   }

   public Cone setColor(String value)
   {
      if (Objects.equals(value, this.color))
      {
         return this;
      }

      final String oldValue = this.color;
      this.color = value;
      this.firePropertyChange(PROPERTY_COLOR, oldValue, value);
      return this;
   }

   public String getMovingDirection()
   {
      return this.movingDirection;
   }

   public Cone setMovingDirection(String value)
   {
      if (Objects.equals(value, this.movingDirection))
      {
         return this;
      }

      final String oldValue = this.movingDirection;
      this.movingDirection = value;
      this.firePropertyChange(PROPERTY_MOVING_DIRECTION, oldValue, value);
      return this;
   }

   public boolean isVisible()
   {
      return this.visible;
   }

   public Cone setVisible(boolean value)
   {
      if (value == this.visible)
      {
         return this;
      }

      final boolean oldValue = this.visible;
      this.visible = value;
      this.firePropertyChange(PROPERTY_VISIBLE, oldValue, value);
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
      this.setPosition(null);
      this.setPlayer(null);
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getColor());
      result.append(' ').append(this.getMovingDirection());
      return result.substring(1);
   }
}
