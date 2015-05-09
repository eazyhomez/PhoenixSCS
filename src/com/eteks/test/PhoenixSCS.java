package com.eteks.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import com.eteks.sweethome3d.io.HomeFileRecorder;
import com.eteks.sweethome3d.model.CatalogPieceOfFurniture;
import com.eteks.sweethome3d.model.CatalogTexture;
import com.eteks.sweethome3d.model.FurnitureCategory;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomeFurnitureGroup;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.HomeRecorder;
import com.eteks.sweethome3d.model.HomeTexture;
import com.eteks.sweethome3d.model.Room;
import com.eteks.sweethome3d.model.TexturesCategory;
import com.eteks.sweethome3d.model.Wall;
import com.eteks.sweethome3d.plugin.Plugin;
import com.eteks.sweethome3d.plugin.PluginAction;


public class PhoenixSCS extends Plugin 
{
	public class RoomTestAction extends PluginAction 
	{		

		public Home home = null;
		public Room room = null;
		//public Room livingRoom = null;
		
		public Wall bckWall = null;
		
		public String homeFilepath = "";	

		// ======================= BOOLEAN FLAGS ======================= //
		
		public boolean bShowMarkerInter = false;
		public boolean bShowMarker = false;
		
		// ======================= CONSTANTS ======================= //
		
		public int MARKBOX_COUNT = 8;
		
		public float FURN_TOLERANCE = 0.51f;
		public float ROOM_TOLERANCE = 0.51f;
		public float WALL_TOLERANCE = 0.1f;
		public float ORIENTATION_TOLERANCE = 0.05f;
		public float SLOPE_TOLERANCE = 0.01f;
		public float VALID_INNERWALL_TOLERANCE = 0.5f;		// 5mm		
		public float FURNITURE_PLACE_TOLERANCE = 0.0f; 		

		public float PLACEMENT_TOLERANCE = 4.0f;			// 4cm
		public float SNAP_TOLERANCE = 76.2f;
		
		public float tolerance = 0.5f; 						// 5 mm

		public float FURNITURE_BLOAT_SIZE = 5.0f;			// 2cm

		public float INFINITY = 10000.0f; 

		public float CONV_IN_M = 100.0f;
		public float CONV_IN_CM = 2.54f;
		public float CONV_FT_CM = (12.0f * CONV_IN_CM);
		public float ROOM_CONV_SQCM_SQFT = 0.00107639104f;
		
		public float VALID_RS_LENGTH = (3.0f * CONV_FT_CM);
		public float DOOR_ELEVATION = (7.0f * CONV_FT_CM);
		public float WALL_HEIGHT = (9.0f * CONV_FT_CM);
		
		public float WALL_PAINTING_HEIGHT = (4.0f * CONV_FT_CM);
		public float WALL_PAINTING_ELEVATION = (3.0f * CONV_FT_CM);
		
		public float ACCESS_CHECK_SIZE = 4.0f * CONV_FT_CM;
		
		public float VALID_SCS_RADIUS = (15.0f * CONV_FT_CM);

		public int validDesignCount = 0;
		
		public Integer orgWallColor = -1184274;
		
		// ======================= GLOBALS ======================= //	
		
		public List<String> furnIds = new ArrayList<String>();
		public List<float[][]> furnRects = new ArrayList<float[][]>();
		public List<float[][]> furnRectsBloated = new ArrayList<float[][]>();	
		public List<Float> furnElevs = new ArrayList<Float>();
		public List<Float> furnHeights = new ArrayList<Float>();
		public List<float[][]> furnRectsAccess = new ArrayList<float[][]>();

		public List<HomePieceOfFurniture> furnList = new ArrayList<HomePieceOfFurniture>();
		public List<List<HomePieceOfFurniture>> catFurnList = new ArrayList<List<HomePieceOfFurniture>>();
		
		public List<String> wallIds = new ArrayList<String>();
		public List<float[][]> wallRects = new ArrayList<float[][]>();
		public List<Float> wallThicks = new ArrayList<Float>();
		public List<Wall> wallLists = new ArrayList<Wall>();

		public List<String> markBoxName = new ArrayList<String>();
		public HomePieceOfFurniture[] markBoxes = new HomePieceOfFurniture[MARKBOX_COUNT];	
		
		public List<SCSConfig> scsConfigList = new ArrayList<SCSConfig>();
		public List<float[][]> scsSeatingConfigList = new ArrayList<float[][]>();
		
		public List<Design> validDesignList = new ArrayList<Design>();
		
		// ======================= SCS CONSTANTS ======================= //
		
		public float SCS_RECT_W = 2.5f;
		public float SCS_RECT_D1 = 4.75f;
		
		public float ROOM_AREA_S_MIN = 0.0f;		
		public float ROOM_AREA_S_MAX = 325.0f;
		
		public float ROOM_AREA_M_MIN = 326.0f;
		public float ROOM_AREA_M_MAX = 500.0f;
		
		public float ROOM_AREA_L_MIN = 501.0f;
		public float ROOM_AREA_L_MAX = 1000.0f;
		
		public float TWO_SEATER_INDEX = 0.0f;
		public int[][] TWO_SEATER_DESIGN_RANGE = {{0,0}};
		
		public float THREE_SEATER_INDEX = 1.0f;
		public int[][] THREE_SEATER_DESIGN_RANGE = {{0,0}};
		
		public float FOUR_SEATER_INDEX = 2.0f;
		public int[][] FOUR_SEATER_DESIGN_RANGE = {{0,0}};
		
		public float FIVE_SEATER_INDEX = 3.0f;
		public int[][] FIVE_SEATER_DESIGN_RANGE = {{0,0}};
		
		public float SIX_SEATER_INDEX = 4.0f;
		public int[][] SIX_SEATER_DESIGN_RANGE = {{0,0}};

		public float[][][] scsDimsArr = {{{ROOM_AREA_M_MIN, ROOM_AREA_M_MAX},{TWO_SEATER_INDEX}}, {{ROOM_AREA_L_MIN, ROOM_AREA_L_MAX},{TWO_SEATER_INDEX}}};
		public int[][][] scsConfigArr = {TWO_SEATER_DESIGN_RANGE, THREE_SEATER_DESIGN_RANGE, FOUR_SEATER_DESIGN_RANGE, FIVE_SEATER_DESIGN_RANGE, SIX_SEATER_DESIGN_RANGE};

		//public float[][][] scsDimsArr = {{{ROOM_AREA_M_MIN, ROOM_AREA_M_MAX},{TWO_SEATER_INDEX, THREE_SEATER_INDEX}}, {{ROOM_AREA_L_MIN, ROOM_AREA_L_MAX},{FOUR_SEATER_INDEX, FIVE_SEATER_INDEX, SIX_SEATER_INDEX}}};
		
		public String[] configLabelArr = {"2 Seater", "3 Seater", "4 Seater", "5 Seater", "6 Seater"};
		
		// Add .obj for base objects too
		public String[] seatingTypeArr = {	"1_seater_sofa",
											"2_seater_sofa",
											"3_seater_sofa",
											"media_cabinet",
											"settee",
											"center_table",
											"corner_table",
											"area_rug",
											"wall_painting"  };

		public float[][] seatingDimsArr = {	{(2.5f*CONV_FT_CM), (2.5f*CONV_FT_CM)},
											{(2.5f*CONV_FT_CM), (2.0f*2.5f*CONV_FT_CM)},
											{(2.5f*CONV_FT_CM), (3.0f*2.5f*CONV_FT_CM)},
											{(2.5f*CONV_FT_CM), (4.0f*CONV_FT_CM)},
											{(2.0f*CONV_FT_CM), (2.5f*2.5f*CONV_FT_CM)},
											{(3.0f*CONV_FT_CM), (2.0f*2.5f*CONV_FT_CM)},
											{(2.0f*CONV_FT_CM), (2.0f*CONV_FT_CM)},
											{(4.0f*CONV_FT_CM), (6.0f*CONV_FT_CM)},
											{(0.25f*CONV_FT_CM), (4.0f*CONV_FT_CM)} };
		
		public int[] seatingPref = {6,0,0,0,1,1,1,0,1};
		
		public String[][] catNamesArr = {{"Armchair"}, {"2_sofa"}, {"3_sofa"}, {"media_cabinet"}, {"chair"}, {"glass table", "coffee table"}, {"corner_table"}, {"area_rug"}, {"wall_painting"}};
		
		public float CENTER_TABLE_HEIGHT = (1.5f * CONV_FT_CM);
		public String catTextArr = "Gray waves wallpaper";
		
		// ======================= CLASSES ======================= //		
		
		public class Points
		{
			float x;
			float y;

			public Points()
			{
				x = -10.0f;
				y = -10.0f;
			}

			public Points(float xCoord , float yCoord)
			{
				x = xCoord;
				y = yCoord;
			}
		}				

		public class LineSegement
		{
			Points startP;		// x, y
			Points endP;		// x, y

			public LineSegement(Points sP, Points eP)
			{
				startP = sP;
				endP = eP;
			}

			public LineSegement(WallSegement ws)
			{
				startP = ws.startP;
				endP = ws.endP;
			}
		}	

		public class WallSegement
		{
			Points startP;		// x, y
			Points endP;		// x, y
			float len;

			public WallSegement(Points sP, Points eP, float l)
			{
				startP = sP;
				endP = eP;
				len = l;
			}
		}

		public class Intersect
		{
			Points p;
			float dist;

			public Intersect(Points inP, float inD)
			{
				p = inP;
				dist = inD;
			}
		}

		public class InterPoints
		{
			Points p;
			boolean bOrg;

			public InterPoints(Points inP, boolean inB)
			{
				p = inP;
				bOrg = inB;
			}
		}

		public class FurnLoc
		{
			float w;
			float h;
			float el;
			float ang;
			Points p;

			public FurnLoc(float wIn, float hIn, float elIn, float angIn, Points coord)
			{
				w = wIn;
				h = hIn;
				el = elIn;
				ang = angIn;
				p = coord;
			}

			public FurnLoc()
			{
				w = 0.0f;
				h = 0.0f;
				el = 0.0f;
				ang = 0.0f;
				p = new Points();
			}
		}

		public class Accessibility
		{
			boolean bAddAccess;
			float accessWidth;
			float accessDepth;

			public Accessibility(boolean bAccess, float accW, float accD)
			{
				bAddAccess = bAccess;
				accessWidth = accW;
				accessDepth = accD;
			}
		}

		public class SCSConfig
		{
			float d;
			float w;
			
			public SCSConfig(float depth , float width)
			{
				d = depth;
				w = width;
			}
		}
		
		public class Design
		{
			HomeFurnitureGroup scsGrp;
			int confIndx;
			int seatingIndx;
			
			public Design(HomeFurnitureGroup fGrp, int cIndx , int sIndx)
			{
				scsGrp = fGrp;
				confIndx = cIndx;
				seatingIndx = sIndx;
			}
		}
				
		public RoomTestAction() 
		{
			putPropertyValue(Property.NAME, "PhoenixSCS");
			putPropertyValue(Property.MENU, "Phoenix-Fresh");

			// Enables the action by default
			setEnabled(true);
		}	

		// ======================= CODE ======================= //

		@Override
		public void execute() 
		{	
			home = getHome();
			room = home.getRooms().get(0);
			
			String hPath = home.getName();
			int indx = hPath.lastIndexOf(".");
			homeFilepath = home.getName().substring(0, indx);
			
			File f = new File(homeFilepath);
			
			if(!f.exists())
				f.mkdir();
			
			
			try
			{
				init();

				storeAllFurnParams(home);
				storeAllWallRects(home);

				markBoxes = getMarkerBoxes();
				
				genConfigList(SCS_RECT_W, SCS_RECT_D1);
				genSeatingConfigList(SCS_RECT_W, SCS_RECT_D1);
				
				long startTime = System.currentTimeMillis(); //System.nanoTime();
				
				readCatalog();
				
				// ============================== PREP  ================================= //
				// 1. Get inner wall segments  ------- //
				
				List<WallSegement> innerWSList = getInnerWalls();
				
				HomePieceOfFurniture pcs = searchMatchFurn("PCSRect");
				
				Points pcsMidP = new Points(pcs.getX(), pcs.getY());
				putMarkers(pcsMidP, 0);
				
				// 2. Calculate valid wall segments  ------- //
				
				List<WallSegement> validWSList = getValidInnerWSInLDRoom(innerWSList, pcsMidP);
				
				List<WallSegement> fWSList = calcFreeWallIntersectionsBelowElev(validWSList, DOOR_ELEVATION, room, 1.0f);
				
				List<WallSegement> finalWSList = shortlistWallSegments(fWSList, VALID_RS_LENGTH);

				validDesignCount = 0;
				int nameCounter = 1;
				
				List<int[][]> activeSCSConfList = getLivingConfigs();
				
				for(int c = 0; c < activeSCSConfList.size(); c++)
				{		
					int[][] scsConfArr = activeSCSConfList.get(c);
					
					for(int x = 0; x < scsConfArr.length; x++)
					{					
						int scsConfIndx = scsConfArr[x][0];
						int scsSeatingIndx = scsConfArr[x][1];
								
						HomePieceOfFurniture scsRect = getFurnItemFromCatalog("SCSRect").clone();
						scsRect.setName("SCSRect_" + nameCounter);
						scsRect.setWidth(scsConfigList.get(scsConfIndx).w);
						scsRect.setDepth(scsConfigList.get(scsConfIndx).d);
						
						placeSCSRectWithSnap(finalWSList, scsRect, innerWSList, validWSList, tolerance, scsSeatingIndx);
						
						nameCounter++;
					}					
				}
				
				// ===================================================================== //
				
				long endTime = System.currentTimeMillis(); //System.nanoTime();				

				JOptionPane.showMessageDialog(null, "No. of Designs generated : " + validDesignCount + "\nTime : " + (endTime - startTime) + " ms");
			}
			catch(Exception e)
			{
				cleanupMarkers();
				
				JOptionPane.showMessageDialog(null," -x-xxx-x- EXCEPTION : " + e.getMessage());						
				//JOptionPane.showMessageDialog(null, "No. of Designs generated : " + validDesignCount);
			}			
		}
		
		public List<int[][]> getLivingConfigs()
		{	
			List<int[][]> activeSCSConfList = new ArrayList<int[][]>();
			List<String> configLabel = new ArrayList<String>();
			
			float roomSize = room.getArea() * ROOM_CONV_SQCM_SQFT;

			for(int x = 0 ; x < scsDimsArr.length; x++)
			{
				if((scsDimsArr[x][0][0] <= roomSize) && (roomSize <= scsDimsArr[x][0][1]))
				{							
					for(int y = 0 ; y < scsDimsArr[x][1].length; y++)
					{
						int indx = new Float(scsDimsArr[x][1][y]).intValue();
						
						int[][] activeConfList = scsConfigArr[indx];
						activeSCSConfList.add(activeConfList);
						configLabel.add(configLabelArr[indx]);
					}
				}
			}
			
			String dbgStr = "roomSize : " + roomSize + " sq. ft. \n\n-------- Initial Configs ---------\n";			
			
			for(int c = 0; c < activeSCSConfList.size(); c++)
			{
				dbgStr += configLabel.get(c) + ": \n";		
				int[][] confL = activeSCSConfList.get(c);
						
				for(int x = 0; x < confL.length; x++)
				{
					if(x == 0)
						dbgStr += (confL[x][1]);
					else
						dbgStr += (", " + confL[x][1]);
				}
				
				dbgStr += "\n\n";
			}
			
			//dbgStr += activePCSConfList.toString() + "\n";						
			//JOptionPane.showMessageDialog(null, dbgStr);
			
			return activeSCSConfList;
		}
		
		public List<WallSegement> getInnerWalls()
		{
			List<WallSegement> wallSegList = new ArrayList<WallSegement>();

			for(int w = 0; w < wallIds.size(); w++)
			{
				List<Points> validPoints = new ArrayList<Points>();

				for(int ws = 0; ws < wallRects.get(w).length; ws++)
				{
					Points p = new Points(wallRects.get(w)[ws][0], wallRects.get(w)[ws][1]);

					if(room.containsPoint(p.x, p.y, (ROOM_TOLERANCE * wallThicks.get(w))))
						validPoints.add(p);
				}

				for(int i = 1; i < validPoints.size(); i++)
				{
					LineSegement ls = new LineSegement( (validPoints.get(i-1)), (validPoints.get(i)) );					

					float dist = calcDistance(ls.startP, ls.endP);
					
					WallSegement ws = new WallSegement(ls.startP, ls.endP, dist);
					wallSegList.add(ws);

					//Points midP = new Points(((ls.startP.x + ls.endP.x) / 2.0f), ((ls.startP.y + ls.endP.y) / 2.0f));
					//putMarkers(midP, 1);			
				}
			}
			
			return wallSegList;
		}
		
		public List<WallSegement> getValidInnerWSInLDRoom(List<WallSegement> inWSList, Points pcsMid)
		{
			List<WallSegement> freeWSList = new ArrayList<WallSegement>();
			
			for(WallSegement ws : inWSList)
			{		
				if(bShowMarkerInter)
				{
					Points wsMid = new Points(((ws.startP.x + ws.endP.x) / 2.0f), ((ws.startP.y + ws.endP.y) / 2.0f));
					putMarkers(wsMid, 7);
				}

				List<Points> validPList = new ArrayList<Points>();					
				List<Points> interList = getIntersectionCircleLine(pcsMid, VALID_SCS_RADIUS, ws.startP, ws.endP);
				
				for(Points p : interList)
				{						
					boolean bInRoom = room.containsPoint(p.x, p.y, tolerance);
					
					if(!bInRoom)
						continue;
					
					boolean bInBetween = checkPointInBetween(p, ws.startP, ws.endP, WALL_TOLERANCE);
					
					if(bInBetween)
						validPList.add(p);
				}
				
				if(validPList.size() > 0)
				{
					List<Points> interPList = new ArrayList<Points>();

					if(calcDistance(pcsMid, ws.startP) >= VALID_SCS_RADIUS)
						interPList.add(ws.startP);							
					
					List<Points> sortedList = sortPList(validPList, ws.startP);
					interPList.addAll(sortedList);
					
					if(calcDistance(pcsMid, ws.endP) >= VALID_SCS_RADIUS)
						interPList.add(ws.endP);
					
					for(int k = 1; k < interPList.size();)
					{
						Points inter1 = interPList.get(k - 1);
						Points inter2 = interPList.get(k);

						WallSegement fws = new WallSegement(inter1, inter2, (calcDistance(inter1, inter2)));							
						freeWSList.add(fws);
						
						k+= 2;
						
						if(bShowMarkerInter)
						{
							Points fwsMid = new Points(((fws.startP.x + fws.endP.x) / 2.0f), ((fws.startP.y + fws.endP.y) / 2.0f));
							putMarkers(fwsMid, 1);
						}
					}
				}
				else
				{
					float dS = calcDistance(pcsMid, ws.startP);
					float dE = calcDistance(pcsMid, ws.endP);
					
					if((dS >= VALID_SCS_RADIUS) && (dE >= VALID_SCS_RADIUS))
					{
						freeWSList.add(ws);
						
						if(bShowMarkerInter)
						{
							Points fwsMid = new Points(((ws.startP.x + ws.endP.x) / 2.0f), ((ws.startP.y + ws.endP.y) / 2.0f));
							putMarkers(fwsMid, 2);
						}
					}
				}
			}
			
			return freeWSList;
		}
		
		public List<WallSegement> shortlistWallSegments(List<WallSegement> inWSList, float reqLen)
		{
			List<WallSegement> finalWSList = new ArrayList<WallSegement>();

			for(WallSegement ws : inWSList)
			{
				if(ws.len >= reqLen)
				{
					finalWSList.add(ws);
					
					if(bShowMarkerInter)
					{
						Points vfwsMid = new Points(((ws.startP.x + ws.endP.x) / 2.0f), ((ws.startP.y + ws.endP.y) / 2.0f));
						putMarkers(vfwsMid, 4);
					}
				}
			}

			return finalWSList;
		}
		
		public void placeSCSRectWithSnap(List<WallSegement> finalWSList, HomePieceOfFurniture scsRect, List<WallSegement> inWSList, List<WallSegement> validWSList, float tolr, int scsSeatingIndx)
		{
			boolean bSuccess = false;
			
			int counter = 1;

			for(WallSegement ws : finalWSList)
			{				
				LineSegement ls = new LineSegement(ws);

				Accessibility accessBox = new Accessibility(false, 0.0f, 0.0f);

				HomePieceOfFurniture hpfP = scsRect.clone();
				hpfP.setName(scsRect.getName() + "_" + counter);
				
				Points scsPoint = calcFurnMids(ws.startP, ws.endP, (0.5f * hpfP.getDepth()), room);
				placeFurnParallelToWall(ls, hpfP, scsPoint);
				
				storeFurnParams(hpfP);
				boolean bIntersects = checkIntersectWithAllFurns(hpfP, accessBox.bAddAccess);		// do not ignore AccBox as of now
				clearFurnParams(hpfP);
				
				//JOptionPane.showMessageDialog(null, "bIntersects : " + bIntersects);
				
				if(!bIntersects)
				{
					HomePieceOfFurniture hpPlaced = searchMatchFurn(hpfP.getName());						
					chkFurnOrient(hpPlaced, ws);		// returns orientation (180*)
					
					boolean bValid = checkAndSnap(hpPlaced, inWSList, tolr);
					
					//JOptionPane.showMessageDialog(null, "bValid : " + bValid);
					
					if(bValid)
					{
						bSuccess = checkInsideHome(finalWSList, hpPlaced, PLACEMENT_TOLERANCE);
						
						if(bSuccess)
						{	
							//JOptionPane.showMessageDialog(null, "bSuccess : " + bSuccess);
							placeRealFurn(hpPlaced, scsSeatingIndx);
						}
					}
					else
					{
						bSuccess = false;
					}
				}
				
				home.deletePieceOfFurniture(hpfP);	
				
				counter++;
			}
		}
		
		public boolean checkAndSnap(HomePieceOfFurniture hpRef, List<WallSegement> inWSList, float tolr)
		{
			List<HomePieceOfFurniture> finalRectList = new ArrayList<HomePieceOfFurniture>();
			
			boolean bSnapped = false;
			
			Points furnCenter = new Points(hpRef.getX(), hpRef.getY());
			
			float[][] fRect = hpRef.getPoints();
			
			//String furnRect = ("furn : " + fRect[0][0] + "," + fRect[0][1] + " / " + fRect[1][0] + "," + fRect[1][1] + " / " + fRect[2][0] + "," + fRect[2][1] + " / " + fRect[3][0] + "," + fRect[3][1] + "\n\n");
			//JOptionPane.showMessageDialog(null, furnRect);
			
			for(int f = 0; f < fRect.length; f++)
			{
				if(f == 2)
					continue;		// Forward snap not needed
				
				Points startP = new Points(fRect[f][0], fRect[f][1]);
				Points endP = null;

				if(f == (fRect.length - 1))
					endP = new Points(fRect[0][0], fRect[0][1]);
				else
					endP = new Points(fRect[f+1][0], fRect[f+1][1]);
				
				LineSegement fs = new LineSegement(startP, endP);
				
				//putMarkers(startP, 2);
				//putMarkers(endP, 2);
						
				for(WallSegement ws : inWSList)
				{					
					LineSegement ls = new LineSegement(ws);
					
					boolean bIsParallel = isParallel(fs, ls, tolr);
					
					if(bIsParallel)
					{
						//Points wsMidP = new Points(((ls.startP.x + ls.endP.x)/2),(ls.startP.y + ls.endP.y)/2);
						//putMarkers(wsMidP, 7);
						
						float dist = calcDistanceParallel(fs, ls, tolr);
						
						if(dist <= SNAP_TOLERANCE)
						{
							List<Points> snapPList = calcSnapCoordinate(ls, fs, dist, tolr);
							
							for(Points snapP : snapPList)
							{
								Points centerFS = new Points(((fs.startP.x + fs.endP.x)/2.0f),(fs.startP.y + fs.endP.y)/2.0f);								
								
								Points snapCoords = new Points((snapP.x - centerFS.x), (snapP.y - centerFS.y));	
								
								hpRef.setX(furnCenter.x + snapCoords.x);
								hpRef.setY(furnCenter.y + snapCoords.y);
								
								//putMarkers(new Points(hpRef.getX(), hpRef.getY()), 6);
								
								boolean bValid = false;
								
								boolean bLiesOnWall = checkFace(hpRef.getPoints(), f, inWSList, tolr);
								
								//JOptionPane.showMessageDialog(null, "bLiesOnWall : " + bLiesOnWall + ", " + f);
										
								if(!bLiesOnWall)
								{
									hpRef.setX(furnCenter.x);
									hpRef.setY(furnCenter.y);
								}
								else
								{
									if(f == 0)
									{
										float currX = hpRef.getX();
										float currY = hpRef.getY();
										
										float newX = (currX + furnCenter.x) / 2.0f;
										float newY = (currY + furnCenter.y) / 2.0f;
										
										hpRef.setX(newX);
										hpRef.setY(newY);
										
										float elongLen = calcDistance(new Points(currX, currY), new Points(furnCenter.x, furnCenter.y));
										
										float currDep = hpRef.getDepth();
										hpRef.setDepth(currDep + elongLen);
										
										//JOptionPane.showMessageDialog(null, "Stretch !!!");
									}
									
									bValid = checkInsideRoom(room, hpRef.getPoints(), PLACEMENT_TOLERANCE);
								}
								
								if(bValid)
								{
									//JOptionPane.showMessageDialog(null, "bValid : " + bValid);
									
									furnCenter = new Points(hpRef.getX(), hpRef.getY());									
									finalRectList.add(hpRef.clone());
																		
									//Points p = new Points(furnCenter.x, furnCenter.y);
									//putMarkers(p, 3);
									
									if(f != 0)
									{
										bSnapped = true;
										break;
									}								
								}
								else
								{
									hpRef.setX(furnCenter.x);
									hpRef.setY(furnCenter.y);
								}
							}								
						}
					}
					
					if(bSnapped)
						break;				
				}
				
				if(bSnapped)
					break;
			}
			
			boolean bInRoom = checkInsideRoom(room, hpRef.getPoints(), PLACEMENT_TOLERANCE);
			
			return bInRoom;
		}

		public void placeRealFurn(HomePieceOfFurniture scsRect, int seatingIndx)
		{			
			bckWall = null;
			
			List<HomePieceOfFurniture> furnList = new ArrayList<HomePieceOfFurniture>();
			List<Integer> refIndxList = new ArrayList<Integer>();
			
			//HomePieceOfFurniture accBox = getFurnItem("accBox").clone();
			HomePieceOfFurniture accBox = getFurnItemFromCatalog("box_invisible").clone();
			
			float[][] scsRectP = scsRect.getPoints();
			
			Points refOrigin = new Points(scsRectP[0][0], scsRectP[0][1]);			
			//putMarkers(refOrigin, 1);
			
			float scsAngle = scsRect.getAngle();
					
			float[][] seatingConf = scsSeatingConfigList.get(seatingIndx);
			
			for(int f = 0; f < seatingConf.length; f++)
			{						
				int furnType = new Float(seatingConf[f][0]).intValue();
				refIndxList.add(furnType);
				
				String furnName = seatingTypeArr[furnType];
				
				//JOptionPane.showMessageDialog(null, seatingConf.length);
				
				float furnX = refOrigin.x + seatingConf[f][1];
				float furnY = refOrigin.y + seatingConf[f][2];
				float furnAng = (((float) (Math.PI * seatingConf[f][3])) / 180.0f);
					
				//JOptionPane.showMessageDialog(null, furnName + " -> " + furnX + ", " + furnY + " : " + furnAng);
				
				HomePieceOfFurniture hpf = getFurnItemFromCatalog(furnName).clone();
				
				hpf.setName(furnType + "_" + furnName + "_" + seatingIndx + "_" + f);
				hpf.setX(furnX);
				
				if(furnName.equalsIgnoreCase("media_cabinet"))
					hpf.setY(furnY - (0.5f*hpf.getDepth()));
				else if(furnName.equalsIgnoreCase("wall_painting"))
				{
					hpf.setHeight(WALL_PAINTING_HEIGHT);
					hpf.setElevation(WALL_PAINTING_ELEVATION);
					hpf.setY(furnY - (0.5f*hpf.getDepth()));
				}
				else
					hpf.setY(furnY);
					
				hpf.setAngle(furnAng);
				furnList.add(hpf);
				
				//JOptionPane.showMessageDialog(null, furnAng);
			}
			
			HomeFurnitureGroup furnGrp = new HomeFurnitureGroup(furnList, (scsRect.getName() + "_Group"));
			List<HomePieceOfFurniture> realFurnList = new ArrayList<HomePieceOfFurniture>();
			
			float grpAng = furnGrp.getAngle();
			
			furnGrp.setX(scsRect.getX());
			furnGrp.setY(scsRect.getY());
			furnGrp.setAngle(scsAngle + grpAng);
			
			//float grpAng2 = furnGrp.getAngle();
			//String dbg = (180.0f * grpAng / (float) Math.PI) + "\n";
			//dbg += (180.0f * scsAngle / (float) Math.PI) + " -> " + (180.0f * grpAng2 / (float) Math.PI);
			//JOptionPane.showMessageDialog(null, dbg);
			
			for(HomePieceOfFurniture hp : furnGrp.getFurniture())
			{
				home.addPieceOfFurniture(hp);
			}
			
			//home.deletePieceOfFurniture(scsRect);	
			
			List<Points> accPList = getAccessbilityPoints(scsRect, (ACCESS_CHECK_SIZE / 2), tolerance);
			
			if(accPList.size() > 1)
			{			
				Points accP1 = accPList.get(0);
				Points accP2 = accPList.get(1);
				
				//putMarkers(accP1, 3);
				//putMarkers(accP2, 3);
				
				float d = calcDistance(accP1, accP2);
				float angle = (float) Math.atan(Math.abs((accP2.y - accP1.y)/(accP2.x - accP1.x)));
				
				try
				{
					accBox.setName("SCSAccessBox");
					accBox.setX((accP1.x + accP2.x)/2.0f);
					accBox.setY((accP1.y + accP2.y)/2.0f);
					accBox.setWidth(d);
					accBox.setDepth(ACCESS_CHECK_SIZE);
					accBox.setAngle(angle);
					home.addPieceOfFurniture(accBox);
					
					// Not running Pathway code for now					
					//PhoenixPathway pathway = new PhoenixPathway();
					//boolean bSuccess = pathway.execute(home, getUserPreferences(), accBox);
					
					boolean bSuccess = true;
					
					if(bSuccess)
					{
						home.deletePieceOfFurniture(accBox);
						//JOptionPane.showMessageDialog(null, "SCS Design generated !!!");
						
						String name = scsRect.getName();
						home.deletePieceOfFurniture(scsRect);
						
						realFurnList = populateFurn(furnGrp, refIndxList);
						
						if(realFurnList.size() > 0)
						{
							saveDesign(home, name);
							validDesignCount++;
						}						

						//Design des = new Design(furnGrp, seatingIndx, p);
						//validDesignList.add(des);
						
						cleanupRealFurnAndWall(realFurnList, bckWall);
					}
					
					cleanupMarkers();
					furnGrp.setAngle(0.0f);
					
				}
				catch(Exception e)
				{
					JOptionPane.showMessageDialog(null," -xxxxxx- EXCEPTION : " + e.getMessage()); 	
					
					if(home != null)
					{
						home.deletePieceOfFurniture(accBox);					
						furnGrp.setAngle(0.0f);
						
						cleanupRealFurnAndWall(realFurnList, bckWall);
					}
				}
				finally
				{
					if(home != null)
					{
						home.deletePieceOfFurniture(accBox);					
						furnGrp.setAngle(0.0f);
						
						cleanupRealFurnAndWall(realFurnList, bckWall);
					}
				}
			}
			else
				JOptionPane.showMessageDialog(null, "Accessibility points not found !!!");
		}
		
		public List<HomePieceOfFurniture> populateFurn(HomeFurnitureGroup furnGrp, List<Integer> refIndxList)
		{		
			List<HomePieceOfFurniture> hpList = new ArrayList<HomePieceOfFurniture>();
			
			for(int h = 0; h <furnGrp.getFurniture().size(); h++)
			{
				HomePieceOfFurniture hp = furnGrp.getFurniture().get(h);
				int prefIndx = seatingPref[refIndxList.get(h)];
				
				String[] nameStr = hp.getName().split("_");
				
				if(nameStr.length > 0)
				{
					int indx = Integer.parseInt(nameStr[0]);
							
					HomePieceOfFurniture realFurn = null;
					
					if(catFurnList.get(indx).size() > prefIndx)
						realFurn = catFurnList.get(indx).get(prefIndx).clone();
					else
						realFurn = catFurnList.get(indx).get(0).clone();				
					
					realFurn.setName(hp.getName());
					realFurn.setX(hp.getX());
					realFurn.setY(hp.getY());
					realFurn.setAngle(hp.getAngle());
					
					// Wallpaper behind Media Cabinet
					if(indx == 7)	
					{
						Points hpMid = new Points(hp.getX(), hp.getY());						
						populateWallFurn(hpMid, catTextArr, 0);

						realFurn.setDepth(hp.getDepth());
					}
					else if(indx == 8)	
					{
						realFurn.setDepth(hp.getDepth());
						realFurn.setHeight(hp.getHeight());
						realFurn.setElevation(hp.getElevation());
					}
					else if(indx != 10)
					{
						realFurn.setWidth(hp.getWidth());
						realFurn.setDepth(hp.getDepth());
					}

					home.addPieceOfFurniture(realFurn);
					home.deletePieceOfFurniture(hp);
					hpList.add(realFurn);
				}
			}
			
			return hpList;
		}
		
		public List<Points> getAccessbilityPoints(HomePieceOfFurniture hp, float accDist, float tolr)
		{
			List<Points> accPList = new ArrayList<Points>();
			
			float[][] fRect = hp.getPoints();
			
			Points startP1 = new Points(fRect[2][0], fRect[2][1]);
			Points endP1 = new Points(fRect[1][0], fRect[1][1]);
			
			Points startP2 = new Points(fRect[3][0], fRect[3][1]);
			Points endP2 = new Points(fRect[0][0], fRect[0][1]);
			
			List<Points> accPList1 = getIntersectionCircleLine(startP1, accDist, startP1, endP1);
			
			for(Points p : accPList1)
			{				
				if(checkPointOnSameSide(p, endP1, startP1, startP2))
					accPList.add(p);
				
				if(bShowMarkerInter)
					putMarkers(p, 5);
			}
			
			List<Points> accPList2 = getIntersectionCircleLine(startP2, accDist, startP2, endP2);
			
			for(Points p : accPList2)
			{				
				if(checkPointOnSameSide(p, endP2, startP1, startP2))
					accPList.add(p);
				
				if(bShowMarkerInter)
					putMarkers(p, 5);
			}
			
			return accPList;
		}
		
		public boolean checkFace(float[][] fRect, int indx, List<WallSegement> inWSList, float tolr)
		{
			boolean bLiesOnWall = false;
			
			Points fStartP = new Points(fRect[indx][0], fRect[indx][1]);
			
			Points fEndP = new Points(fRect[0][0], fRect[0][1]);
			
			if((indx+1) < fRect.length)
				fEndP = new Points(fRect[indx+1][0], fRect[indx+1][1]);
			
			for(WallSegement ws : inWSList)
			{
				LineSegement ls = new LineSegement(ws);
				
				boolean b1 = checkPointInBetween(fStartP, ls.startP, ls.endP, tolr);
				boolean b2 = checkPointInBetween(fEndP, ls.startP, ls.endP, tolr);
				
				if(b1 && b2)
				{
					bLiesOnWall = true;
					break;
				}
				
				//if(!b1)
				{
					//float lenF = calcDistance(fStartP, fEndP);
					//float lenW = calcDistance(ls.startP, ls.endP);
				}	
				
				b1 = checkPointInBetween(ls.startP, fStartP, fEndP, tolr);				
				b2 = checkPointInBetween(ls.endP, fStartP, fEndP, tolr);
				
				if(b1 && b2)
				{
					bLiesOnWall = true;
					break;
				}
			}
			
			return bLiesOnWall;
		}
			
		public void populateWallFurn(Points midP, String textName, int prefIndx)
		{				
			float minDist = INFINITY;
			float[][] wRect = new float[0][0];
			
			for(int w = 0; w < wallLists.size(); w++)
			{
				Wall ws = wallLists.get(w);
				Points wallMidP = new Points(((ws.getXStart() + ws.getXEnd())/2.0f), ((ws.getYStart() + ws.getYEnd())/2.0f));
				
				float d = calcDistance(wallMidP, midP);
				
				if(d < minDist)
				{
					minDist = d;
					bckWall = ws;
					wRect = wallRects.get(w);
				}
			}
			
			Points wallSP = new Points(bckWall.getXStart(), bckWall.getYStart());			
			Points wallEP = new Points(bckWall.getXEnd(), bckWall.getYEnd());
			Points wallMP =  new Points(((wallSP.x + wallEP.x) / 2), ((wallSP.y + wallEP.y) / 2));
			
			Points w0 = new Points(wRect[0][0], wRect[0][1]);
			Points w1 = new Points(wRect[1][0], wRect[1][1]);
			Points w2 = new Points(wRect[2][0], wRect[2][1]);
			Points w3 = new Points(wRect[3][0], wRect[3][1]);

			List<HomeTexture> htList = searchMatchTexture(textName);
			
			boolean b1 = checkPointOnSameSide(midP, wallMP, w0, w1);
			
			if(b1)
			{
				//Points midWS =  new Points(((w0.x + w1.x) / 2), ((w0.y + w1.y) / 2));
				//putMarkers(midWS, 1);
				
				if(htList.size() > prefIndx)
					bckWall.setRightSideTexture(htList.get(prefIndx));	// apply on right side
				else if(htList.size() > 0)
					bckWall.setRightSideTexture(htList.get(0));
			}
			
			boolean b2 = checkPointOnSameSide(midP, wallMP, w2, w3);
			
			if(b2)
			{	
				//Points midWS =  new Points(((w0.x + w1.x) / 2), ((w0.y + w1.y) / 2));
				//putMarkers(midWS, 3);
				
				if(htList.size() > prefIndx)
					bckWall.setLeftSideTexture(htList.get(prefIndx)); // apply on left side
				else if(htList.size() > 0)
					bckWall.setLeftSideTexture(htList.get(0));
			}
			
			//JOptionPane.showMessageDialog(null, "b1 : " + b1 + ", b2 : " + b2);
		}
		
		public List<WallSegement> calcFreeWallIntersectionsBelowElev(List<WallSegement> validWSList, float elv, Room r, float tolr)
		{
			List<WallSegement> freeWallSegList = new ArrayList<WallSegement>();

			// Compare which furn obj have elevation less than "elv"
			// Take intersection points for objects whose elevation < "elv"

			try
			{
				for(WallSegement ws : validWSList)
				{
					TreeMap<Float, Intersect> interMap = new TreeMap<Float, Intersect>();

					Intersect wallS = new Intersect(ws.startP, 0.0f);
					interMap.put(0.0f, wallS);

					Intersect wallE = new Intersect(ws.endP, ws.len);
					interMap.put(ws.len, wallE);

					// Debug
					//Points midPWS = new Points(((ws.startP.x + ws.endP.x)/2.0f),((ws.startP.y + ws.endP.y)/2.0f));
					//putMarkers(midPWS, 5);

					for(int f = 0; f < furnElevs.size(); f++)
					{
						float furnElev = furnElevs.get(f);

						if(elv >= furnElev)
						{							
							LineSegement ref = new LineSegement(ws.startP, ws.endP);								
							List<Intersect> interList = checkIntersect(ref, furnIds.get(f));

							int interCount = 0;

							for(Intersect inter : interList)
							{
								//if(r.containsPoint(inter.p.x, inter.p.y, tolr))
								if(checkPointInBetween(inter.p, ws.startP, ws.endP, tolr))
								{			
									interCount++;

									interMap.put(inter.dist, inter);

									if(bShowMarkerInter)
										putMarkers(inter.p, 3);
								}
							}

							if(interCount == 1)
							{
								float X = furnList.get(f).getX();
								float Y = furnList.get(f).getY();

								Points midP = new Points(X, Y);

								float calcDS = calcDistance(midP, ws.startP);
								float calcDE = calcDistance(midP, ws.endP);

								Intersect inter;

								//if((calcDS <= calcDE) && (calcDS <= tolr))
								if(calcDS <= calcDE)
								{
									inter = new Intersect(ws.startP, 0.5f);
									interMap.put(inter.dist, inter);

									//if(bShowMarkerInter)
									//putMarkers(inter.p, 4);
								}
								//else if(calcDE <= tolr)
								else
								{
									inter = new Intersect(ws.endP, (ws.len - 0.5f));
									interMap.put(inter.dist, inter);

									//if(bShowMarkerInter)
									//putMarkers(inter.p, 4);
								}
							}
						}
					}

					// Truncate the map so that end point is ws.endP	
					NavigableMap<Float, Intersect> interSet = interMap.headMap(ws.len, true);

					Set<Float> keys = interSet.keySet();
					List<Intersect> inList = new ArrayList<Intersect>();

					for(Float k : keys)
					{
						inList.add(interSet.get(k));
					}					

					for(int k = 1; k < inList.size();)
					{
						Intersect inter1 = inList.get(k - 1);
						Intersect inter2 = inList.get(k);

						WallSegement fws = new WallSegement(inter1.p, inter2.p, (inter2.dist - inter1.dist));
						
						freeWallSegList.add(fws);
						
						if(bShowMarker)
						{
							putMarkers(fws.startP, 1);
							putMarkers(fws.endP, 2);
						}
						
						k+= 2;
					}
				}
			}
			catch(Exception e) 
			{
				JOptionPane.showMessageDialog(null," -x-x-x- EXCEPTION [calcFreeWallIntersectionsBelowElev]: " + e.getMessage()); 
				e.printStackTrace();
			}

			return freeWallSegList;
		}
		
		// ======================= INIT FUNCTIONS ======================= //

		public void init()
		{
			furnIds = new ArrayList<String>();
			furnRects = new ArrayList<float[][]>();
			furnRectsBloated = new ArrayList<float[][]>();

			wallIds = new ArrayList<String>();
			wallRects = new ArrayList<float[][]>();			
			wallThicks = new ArrayList<Float>();
		}		

		public void storeAllFurnParams(Home h)
		{	
			//String dbgStr = "";
			
			for(HomePieceOfFurniture hp: h.getFurniture())
			{
				String fName = hp.getName();
				
				if(!markBoxName.contains(fName))
				{			
					//dbgStr += hp.getName() + "\n";
					
					furnIds.add(hp.getName());
					furnRects.add(hp.getPoints());
					furnRectsAccess.add(hp.getPoints());
					furnElevs.add(hp.getElevation());
					furnHeights.add(hp.getHeight());
					furnList.add(hp);

					HomePieceOfFurniture hClone = hp.clone();
					float d = hp.getDepth();
					float w = hp.getWidth();

					hClone.setDepth(d + FURNITURE_BLOAT_SIZE);
					hClone.setWidth(w + FURNITURE_BLOAT_SIZE);
					hClone.setElevation(0.0f);

					furnRectsBloated.add(hClone.getPoints());
				}
			}
			
			//JOptionPane.showMessageDialog(null, dbgStr);
		}

		public void storeFurnParams(HomePieceOfFurniture hpf)
		{			
			String fName = hpf.getName();

			if(!markBoxName.contains(fName) )
			{
				furnIds.add(hpf.getName());
				furnRects.add(hpf.getPoints());

				HomePieceOfFurniture hClone = hpf.clone();
				float d = hpf.getDepth();
				float w = hpf.getWidth();

				hClone.setDepth(d + FURNITURE_BLOAT_SIZE);
				hClone.setWidth(w + FURNITURE_BLOAT_SIZE);
				hClone.setElevation(0.0f);

				furnRectsBloated.add(hClone.getPoints());
			}
		}
		
		public void clearFurnParams(HomePieceOfFurniture hpf)
		{			
			String fName = hpf.getName();

			if(!markBoxName.contains(fName) )
			{
				int indx = furnIds.indexOf(hpf.getName());
				
				if(indx > -1)
				{
					furnIds.remove(indx);
					furnRects.remove(indx);
					furnRectsBloated.remove(indx);
				}
			}
		}

		public void storeAllWallRects(Home h)
		{
			int wallCount = 1;

			//String furnRect = "";

			for(Wall w: h.getWalls())
			{
				wallIds.add("wall_" + wallCount);

				float[][] wRect = w.getPoints();
				wallRects.add(wRect);
				wallThicks.add(w.getThickness());		
	
				wallLists.add(w);
				
				w.setHeight(WALL_HEIGHT);
				//furnRect = ("Wall_"+ wallCount +" : " + wRect[0][0] + "," + wRect[0][1] + " / " + wRect[1][0] + "," + wRect[1][1] + " / " + wRect[2][0] + "," + wRect[2][1] + " / " + wRect[3][0] + "," + wRect[3][1] + "\n\n");

				wallCount++;
			}

			//JOptionPane.showMessageDialog(null, furnRect);
		}

		public void readCatalog()
		{
			for(int s = 0; s < seatingTypeArr.length; s++)
			{						
				float d = seatingDimsArr[s][0];
				float w = seatingDimsArr[s][1];
				
				List<HomePieceOfFurniture> furnList = new ArrayList<HomePieceOfFurniture>();
				
				for(String fName : catNamesArr[s])
				{
					List<HomePieceOfFurniture> fList =  searchCatalog(fName, w, d);
					furnList.addAll(fList);
					
					JOptionPane.showMessageDialog(null, fName + " : " + fList.size() + " >>>> " + w + ", " + d);
				}
				
				catFurnList.add(s, furnList);
			}			
		}
		
		public void genConfigList(float x, float y1)
		{
			scsConfigList = new ArrayList<SCSConfig>();
			
			// Config 1 : 1 - 4.75 ft
			SCSConfig scsConf = new SCSConfig((y1 + x)*CONV_FT_CM, ((2*x) + 2.0f)*CONV_FT_CM);
			scsConfigList.add(scsConf);
		}
		
		public void genSeatingConfigList(float x, float y1)
		{
			scsSeatingConfigList = new ArrayList<float[][]>();
			
			// -------------------------------- 4 Seater -------------------------------- //
			
			// Seating Config 1
			float[][] seatingConf1 = {	{0.0f, (x*0.5f + 1.0f)*CONV_FT_CM, (x*0.5f)*CONV_FT_CM, -45.0f},
										{0.0f, ((3.0f*x*0.5f) + 2.0f)*CONV_FT_CM, (x*0.5f)*CONV_FT_CM, 45.0f},	
										{6.0f, (x + 1.0f)*CONV_FT_CM, ((3.0f*x*0.5f) + 1.0f)*CONV_FT_CM, 180.0f},
										{7.0f, (x + 1.0f)*CONV_FT_CM, ((3.0f*x*0.5f) + 1.0f)*CONV_FT_CM, 180.0f},
										{8.0f, (x + 1.0f)*CONV_FT_CM, (y1 + x)*CONV_FT_CM, 180.0f}  };
			
			scsSeatingConfigList.add(seatingConf1);			
		}
		
		// ======================= UTIL FUNCTIONS ======================= //
		
		public List<HomePieceOfFurniture> searchCatalog(String furnName, float width, float depth)
		{
			List<HomePieceOfFurniture> furnList = new ArrayList<HomePieceOfFurniture>();
			List<String> typeArr = Arrays.asList(seatingTypeArr);
			
			//String dbgStr = "";
			
			float w = width * CONV_IN_M;
			float d = depth * CONV_IN_M;
			
			HomePieceOfFurniture matchFurn = null;
			List<FurnitureCategory> fCatg = getUserPreferences().getFurnitureCatalog().getCategories();		

			try 
			{
				for(int c = 0; c < fCatg.size(); c++ )
				{
					List<CatalogPieceOfFurniture> catPOFList = fCatg.get(c).getFurniture();

					for(int p = 0; p < catPOFList.size(); p++ )
					{
						CatalogPieceOfFurniture catF = catPOFList.get(p);
						
						if(catF.getName().toLowerCase().contains(furnName.toLowerCase()))
						{
							if(typeArr.contains(catF.getName()))
								continue; 
							
							//JOptionPane.showMessageDialog(null,catF.getName() + " -> w : " + w + " cm, d : " + d + " cm \n");
							
							matchFurn = new HomePieceOfFurniture(catF);
							
							float cW = matchFurn.getWidth();
							float cD = matchFurn.getDepth();
							
							if(furnName.contains("table"))
							{
								float cH = matchFurn.getHeight();
								
								if((cW <= w) && (cD <= d) && (cH <= CENTER_TABLE_HEIGHT))
								{
									furnList.add(matchFurn);
									//JOptionPane.showMessageDialog(null,catF.getName() + " -> w : " + cW + " cm, h : " + cH + " cm \n"); 
								}
							}
							else
							{
								if((cW <= w) && (cD <= d))
								{
									furnList.add(matchFurn);
									//dbgStr += catF.getName() + " -> w : " + cW + " cm, d : " + cD + " cm \n"; 
								}
							}
							
						}
					}	
				}				
			}
			catch(Exception e){e.printStackTrace();}

			//JOptionPane.showMessageDialog(null, dbgStr);
			return furnList;
		}
		
		public void saveDesign(Home h, String name)
		{
			try
			{
				Home hClone = h.clone();
				hClone.setName(name + "_Home");
				
				HomeRecorder recorder = new HomeFileRecorder();
				recorder.writeHome(h, (homeFilepath + File.separatorChar + name  + "_Home" + ".sh3d")); 
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(null, " x-x-x EXCEPTION while saving design !!!");		
			}
		}
		
		public void cleanupMarkers()
		{
			for(HomePieceOfFurniture hpf : home.getFurniture())
			{
				if(markBoxName.contains(hpf.getName()))
					home.deletePieceOfFurniture(hpf);
			}
		}	
		
		public void cleanupRealFurnAndWall(List<HomePieceOfFurniture> fList, Wall w)
		{					
			for(HomePieceOfFurniture hpf : fList)
			{
				home.deletePieceOfFurniture(hpf);
			}
			
			if(w != null)
			{
				w.setRightSideColor(orgWallColor);
				w.setRightSideTexture(null);
				
				w.setLeftSideColor(orgWallColor);
				w.setLeftSideTexture(null);
			}
		}
		
		public boolean checkPointOnSameSide(Points a, Points b, Points pS1, Points pS2)
		{
			boolean bRet = false;
			
			// ((y1−y2)(ax−x1)+(x2−x1)(ay−y1))((y1−y2)(bx−x1)+(x2−x1)(by−y1)) < 0
			
			float res = ( ((pS1.y - pS2.y)*(a.x - pS1.x)) + ((pS2.x - pS1.x)*(a.y - pS1.y)) )*( ((pS1.y - pS2.y)*(b.x - pS1.x)) + ((pS2.x - pS1.x)*(b.y - pS1.y)) );
			
			if(res < 0)
				bRet = false;
			else
				bRet = true;
			
			return bRet;
		}
		
		public List<Points> calcSnapCoordinate(LineSegement ws, LineSegement ls, float dist, float tolr) 
		{
			List<Points> retPList = new ArrayList<Points>();
			
			Points wsMidP = new Points(((ws.startP.x + ws.endP.x)/2.0f),(ws.startP.y + ws.endP.y)/2.0f);
			
			Points centerP = new Points(((ls.startP.x + ls.endP.x)/2.0f),(ls.startP.y + ls.endP.y)/2.0f);
			
			//putMarkers(ws.startP, 5);
			//putMarkers(ws.endP, 5);
			
			float xLimit = Math.abs(ws.endP.x - ws.startP.x);
			float yLimit = Math.abs(ws.endP.y - ws.startP.y);
			
			//JOptionPane.showMessageDialog(null, "xLimit:" + xLimit + ", yLimit:" + yLimit + ", tolr:" + tolr);
					 
			if(yLimit < tolr)
			{
				// Perpendicular - towards wall
				if(yLimit < tolr)
				{
					Points p1 = new Points(centerP.x, (centerP.y + dist));
					Points p2 = new Points(centerP.x, (centerP.y - dist));
					
					//JOptionPane.showMessageDialog(null, "1_ p1 : " + p1.x + ", " + p1.y + ",\np2 : " + p2.x + ", " + p2.y);
					
					List<Points> interPList2 = new ArrayList<Points>();
					interPList2.add(p1);
					interPList2.add(p2);
					
					List<Points> sortedPList2 = sortPList(interPList2, wsMidP);
					retPList.addAll(sortedPList2);
				}
				else if(yLimit >= tolr)
				{
					float slopePara = ((ws.endP.y - ws.startP.y) / (ws.endP.x - ws.startP.x));
					float slopePerp = (-1.0f / slopePara);
					float intercept = centerP.y - (slopePerp * centerP.x);
					
					//JOptionPane.showMessageDialog(null, "1_ slopePara : " + slopePara + ",\nslopePerp : " + slopePerp);
					
					List<Points> interPList2 = getIntersectionCircleLine2(centerP, dist, slopePerp, intercept);
					List<Points> sortedPList2 = sortPList(interPList2, wsMidP);
					
					retPList.addAll(sortedPList2);
				}
				
				//JOptionPane.showMessageDialog(null, slopePerp + "/ interceptPerp : " + intercept);
			}
			else if(xLimit < tolr)
			{
				// Perpendicular - towards wall
				if(xLimit < tolr)
				{
					Points p1 = new Points((centerP.x + dist), centerP.y);
					Points p2 = new Points((centerP.x - dist), centerP.y);
					
					//JOptionPane.showMessageDialog(null, "2_ p1 : " + p1.x + ", " + p1.y + ",\np2 : " + p2.x + ", " + p2.y);
							
					List<Points> interPList1 = new ArrayList<Points>();
					interPList1.add(p1);
					interPList1.add(p2);
					
					List<Points> sortedPList1 = sortPList(interPList1, wsMidP);
					retPList.addAll(sortedPList1);
				}
				else if(xLimit >= tolr)
				{
					float slopePara = ((ws.endP.y - ws.startP.y) / (ws.endP.x - ws.startP.x));
					float slopePerp = (-1.0f / slopePara);
					float intercept = centerP.y - (slopePerp * centerP.x);
					
					//JOptionPane.showMessageDialog(null, "2_ slopePara : " + slopePara + ",\nslopePerp : " + slopePerp);
					
					List<Points> interPList1 = getIntersectionCircleLine2(centerP, dist, slopePerp, intercept);				
					List<Points> sortedPList1 = sortPList(interPList1, wsMidP);
					
					retPList.addAll(sortedPList1);
				}			
				//JOptionPane.showMessageDialog(null, slopePerp + "/ interceptPerp : " + intercept);				
			}
			else
			{
				// Perpendicular - towards longest wall
				if(yLimit < tolr)
				{
					Points p1 = new Points(centerP.x, (centerP.y + dist));
					Points p2 = new Points(centerP.x, (centerP.y - dist));
					
					//JOptionPane.showMessageDialog(null, "3_ p1 : " + p1.x + ", " + p1.y + ",\np2 : " + p2.x + ", " + p2.y);
					
					List<Points> interPList2 = new ArrayList<Points>();
					interPList2.add(p1);
					interPList2.add(p2);
					
					List<Points> sortedPList2 = sortPList(interPList2, wsMidP);
					retPList.addAll(sortedPList2);
				}
				else if(yLimit >= tolr)
				{
					float slopePara = ((ws.endP.y - ws.startP.y) / (ws.endP.x - ws.startP.x));
					float slopePerp = (-1.0f / slopePara);
					float intercept = centerP.y - (slopePerp * centerP.x);
					
					//JOptionPane.showMessageDialog(null, "3_ slopePara : " + slopePara + ",\nslopePerp : " + slopePerp);
					
					List<Points> interPList2 = getIntersectionCircleLine2(centerP, dist, slopePerp, intercept);
					List<Points> sortedPList2 = sortPList(interPList2, wsMidP);
					
					retPList.addAll(sortedPList2);
				}			
				//JOptionPane.showMessageDialog(null, slopePerp + "/ interceptPerp : " + intercept);
			}
			
			List<Points> sortedPList = sortPList(retPList, wsMidP);
			
			for(int p = 0 ; p < sortedPList.size(); p++)
			{
				Points pt = sortedPList.get(p);
				
				if(!room.containsPoint(pt.x, pt.y, ROOM_TOLERANCE))
					sortedPList.remove(p);
				
				//putMarkers(p, 3);
			}

			return sortedPList;
		}
		
		public List<Points> getIntersectionCircleLine2(Points center, float rad, float slope, float intercept)
		{
			List<Points> interList = new ArrayList<Points>();
			
			try
			{	
				// Equation of Line
				float m = slope;
				float c = intercept;
				
				// (m^2+1)x^2 + 2(mca��mq−p)x + (q^2−r^2+p^2−2cq+c^2) = 0			
				
				float A = (m*m) + 1;
				float B = 2*((m*c) - (m*center.y) - center.x);
				float C = (center.y*center.y) - (rad*rad) + (center.x*center.x) - 2*(c*center.y) + (c*c);
				
				float D = (B*B) - 4*A*C;
				
				if(D == 0)
				{
					float x1 = ((-B) + (float)Math.sqrt(D)) / (2*A);
					float y1 = (m*x1) + c;
					
					Points inter = new Points(x1, y1);
					interList.add(inter);	
					
					//putMarkers(inter, true);
				}
				else if (D > 0)
				{
					float x1 = ((-B) + (float)Math.sqrt(D)) / (2*A);
					float y1 = (m*x1) + c;
					
					Points inter1 = new Points(x1, y1);
					interList.add(inter1);
					
					//putMarkers(inter1, false);
					
					float x2 = ((-B) - (float)Math.sqrt(D)) / (2*A);
					float y2 = (m*x2) + c;
					
					Points inter2 = new Points(x2, y2);
					interList.add(inter2);
					
					//putMarkers(inter2, false);
				}		
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(null," -xxxxx- EXCEPTION : " + e.getMessage()); 
				e.printStackTrace();
			}
			
			return interList;
		}
		
		public float calcDistanceParallel(LineSegement ls1, LineSegement ls2, float tolr)
		{
			float xLimit = Math.abs(ls1.endP.x - ls1.startP.x);
			float yLimit = Math.abs(ls1.endP.y - ls1.startP.y);
			
			float d = 0.0f;
			
			if(xLimit < tolr)
			{
				d = Math.abs(ls2.endP.x - ls1.endP.x);
			}
			else if(yLimit < tolr)
			{
				d = Math.abs(ls2.endP.y - ls1.endP.y);
			}
			else
			{			
				float M = (ls1.endP.y - ls1.startP.y) / (ls1.endP.x - ls1.startP.x);									// (y2-y1)/(x2-x1)
				
				float B1 = ((ls1.startP.y * ls1.endP.x) - (ls1.endP.y * ls1.startP.x)) / (ls1.endP.x - ls1.startP.x);	// (y1x2 - y2x1)/(x2-x1)
				float B2 = ((ls2.startP.y * ls2.endP.x) - (ls2.endP.y * ls2.startP.x)) / (ls2.endP.x - ls2.startP.x);
				
				d = (Math.abs(B2 - B1) / ((float) Math.sqrt((M*M) + 1)));
			}
			
			return d;
		}
		
		public HomePieceOfFurniture searchMatchFurn(String furnName)
		{
			HomePieceOfFurniture matchFurn = null;
			
			try 
			{				
				List<HomePieceOfFurniture> catPOF = home.getFurniture();

				for(int p = 0; p < catPOF.size(); p++ )
				{
					if(furnName.equalsIgnoreCase(catPOF.get(p).getName()))
					{
						matchFurn = catPOF.get(p);
						break;
					}
				}			
			}
			catch(Exception e){e.printStackTrace();}

			return matchFurn;
		}
		
		public List<HomeTexture> searchMatchTexture(String textName)
		{			
			List<HomeTexture> txtList = new ArrayList<HomeTexture>();
			List<TexturesCategory> fCatg = getUserPreferences().getTexturesCatalog().getCategories();
			
			try 
			{
				for(int c = 0; c < fCatg.size(); c++ )
				{
					List<CatalogTexture> catTxtList = fCatg.get(c).getTextures();
					
					for(int p = 0; p < catTxtList.size(); p++ )
					{						
						CatalogTexture catT = catTxtList.get(p);
						
						//JOptionPane.showMessageDialog(null, catT);
						
						if(catT.getName().equalsIgnoreCase(textName))
						{							
							//JOptionPane.showMessageDialog(null, catT.getName());	
							txtList.add(new HomeTexture(catT));							
						}
					}
				}				
			}
			catch(Exception e){JOptionPane.showMessageDialog(null, e.getMessage()); e.printStackTrace();}
			
			return txtList;
		}
		
		public Points calcFurnMids(Points p1, Points p2, float d, Room inRoom)
		{
			Points retPoints = new Points();

			float l = calcDistance(p1,p2);
			float r = (float)Math.sqrt((d*d) + (0.25f*l*l));

			float e = (p2.x - p1.x);
			float f = (p2.y - p1.y);
			float p = (float)Math.sqrt((e*e + f*f));
			float k = (0.5f * p);

			float x1 = p1.x + (e*k/p) + (f/p)*((float)Math.sqrt((r*r - k*k)));
			float y1 = p1.y + (f*k/p) - (e/p)*((float)Math.sqrt((r*r - k*k)));

			float x2 = p1.x + (e*k/p) - (f/p)*((float)Math.sqrt((r*r - k*k)));
			float y2 = p1.y + (f*k/p) + (e/p)*((float)Math.sqrt((r*r - k*k)));

			// Check for in Room
			if(inRoom.containsPoint(x1, y1, 0.0f))
			{
				retPoints = new Points(x1, y1);
			}
			else if(inRoom.containsPoint(x2, y2, 0.0f))
			{
				retPoints = new Points(x2, y2);
			}

			return retPoints;

			/*
			 	Let the centers be: (a,b), (c,d)
				Let the radii be: r, s

				  e = c - a                          [difference in x coordinates]
				  f = d - b                          [difference in y coordinates]
				  p = sqrt(e^2 + f^2)                [distance between centers]
				  k = (p^2 + r^2 - s^2)/(2p)         [distance from center 1 to line joining points of intersection]


				  x = a + ek/p + (f/p)sqrt(r^2 - k^2)
				  y = b + fk/p - (e/p)sqrt(r^2 - k^2)
				OR
				  x = a + ek/p - (f/p)sqrt(r^2 - k^2)
				  y = b + fk/p + (e/p)sqrt(r^2 - k^2)		
			 */
		}

		public float chkFurnOrient(HomePieceOfFurniture furn, WallSegement ws)
		{		
			float rotation = 0.0f;
					
			float[][] fRect = furn.getPoints();

			//String furnRect = ("furn : " + fRect[0][0] + "," + fRect[0][1] + " / " + fRect[1][0] + "," + fRect[1][1] + " / " + fRect[2][0] + "," + fRect[2][1] + " / " + fRect[3][0] + "," + fRect[3][1] + "\n\n");
			//JOptionPane.showMessageDialog(null, furnRect);

			Points furnBottMid = new Points(((fRect[2][0] + fRect[3][0]) / 2),  ((fRect[2][1] + fRect[3][1]) / 2));

			Points wsMid = new Points(((ws.startP.x + ws.endP.x) / 2),  ((ws.startP.y + ws.endP.y) / 2));

			float dist = calcDistance(furnBottMid, wsMid);

			if(dist > FURN_TOLERANCE)
			{
				float ang = furn.getAngle();

				furn.setAngle(ang + (float)Math.PI);
				rotation = 180.0f;
				
				//JOptionPane.showMessageDialog(null, "rotated 180");
			}
			//else
				//JOptionPane.showMessageDialog(null, "No rotation !!!");
			
			return rotation;
		}

		public float[][] genAccessBox(HomePieceOfFurniture hpf, float width, float depth)
		{
			HomePieceOfFurniture hpfC = hpf.clone();
			hpfC.setWidth(hpf.getWidth() + (2*width));
			hpfC.setDepth(hpf.getDepth() + (2*depth));

			float[][] accessRect = hpfC.getPoints();

			return accessRect;
		}

		public void placeFurnParallelToWall(LineSegement ws, HomePieceOfFurniture furn, Points furnCoords)
		{
			FurnLoc furnLoc = new FurnLoc();
			float furnAngle = calcWallAngles(ws);

			furnLoc.w = furn.getWidth();
			furnLoc.ang = furnAngle;			
			furnLoc.p = furnCoords;	

			placeFurnItem(furn, furnLoc);
		}

		public void placeFurnItem(HomePieceOfFurniture inFurn, FurnLoc fLoc)
		{
			inFurn.setWidth(fLoc.w);
			inFurn.setAngle(fLoc.ang);
			inFurn.setX(fLoc.p.x);
			inFurn.setY(fLoc.p.y);

			home.addPieceOfFurniture(inFurn);
		}

		public float calcWallAngles(LineSegement ws)
		{
			float retAngle = 0.0f;

			float wsAngle =  (float) Math.atan((Math.abs(ws.endP.y - ws.startP.y)) / (Math.abs(ws.endP.x - ws.startP.x))); 

			Points p = new Points((ws.startP.x - ws.endP.x), (ws.startP.y - ws.endP.y));
			int qIndx = getQuadrantInfo(p);

			if(qIndx == 1)
				retAngle = wsAngle;
			else if(qIndx == 2)
				retAngle = (float)(Math.PI) - wsAngle;
			else if(qIndx == 3)
				retAngle = (float)(Math.PI) + wsAngle;
			else if(qIndx == 4)
				retAngle = (float)(2.0f*Math.PI) - wsAngle;

			//JOptionPane.showMessageDialog(null, "angle : " + wsAngle + " -> "+ (retAngle * 180.0f / (float) Math.PI) + ", " + qIndx);

			return retAngle;
		}

		public int getQuadrantInfo(Points p)
		{
			int qIndx = 0;

			if((p.x >= 0.0f) && (p.y > 0.0f))
				qIndx = 1;
			else if((p.x < 0.0f) && (p.y >= 0.0f))
				qIndx = 2;
			else if((p.x <= 0.0f) && (p.y < 0.0f))
				qIndx = 3;
			else if((p.x > 0.0f) && (p.y <= 0.0f))
				qIndx = 4;

			return qIndx;
		}

		public void chkFurnOrient(HomePieceOfFurniture furn, LineSegement ws)
		{			
			float[][] fRect = furn.getPoints();
			Points furnBottMid = new Points(((fRect[2][0] + fRect[3][0]) / 2),  ((fRect[2][1] + fRect[3][1]) / 2));

			Points wsMid = new Points(((ws.startP.x + ws.endP.x) / 2),  ((ws.startP.y + ws.endP.y) / 2));

			float dist = calcDistance(furnBottMid, wsMid);
			//JOptionPane.showMessageDialog(null, "dist : " + dist);

			if(dist > ORIENTATION_TOLERANCE)
			{
				furn.setAngle((float)Math.PI);
				//JOptionPane.showMessageDialog(null, "180 rotation");
			}
		}

		public boolean checkIntersectWithAllFurns(HomePieceOfFurniture hpf, boolean bAddAccessibility)
		{
			boolean bIntersects = false;

			for(int x = 0 ; x < furnIds.size(); x++)
			{					
				if(!hpf.getName().equalsIgnoreCase(furnIds.get(x)))
				{	
					// Ignore windows
					if(furnIds.get(x).toLowerCase().contains("window"))
						continue;
						
					float[][] refFurnRect = furnRects.get(x);

					for(int f = 0; f < refFurnRect.length; f++)
					{
						Points startLine = new Points(refFurnRect[f][0], refFurnRect[f][1]);

						Points endLine = null;

						if(f == (refFurnRect.length - 1))
							endLine = new Points(refFurnRect[0][0], refFurnRect[0][1]);
						else
							endLine = new Points(refFurnRect[f+1][0], refFurnRect[f+1][1]);				

						LineSegement ls = new LineSegement(startLine, endLine);

						// For Accessibility check
						List<Intersect> interList = new ArrayList<Intersect>();

						if(bAddAccessibility)
							interList = checkIntersectAccessibility(ls, hpf.getName());
						else
							interList = checkIntersect(ls, hpf.getName());

						for(Intersect inter : interList)
						{
							if(inter != null)
							{
								bIntersects = checkPointInBetween(inter.p, ls.startP, ls.endP, FURN_TOLERANCE);

								if(bIntersects)
									break;
							}
							//putMarkers(inter.p, 3);
						}
					}

					if(bIntersects)
						break;
				}

				if(bIntersects)
					break;
			}

			return bIntersects;
		}
		
		public boolean checkIntersectWSWithAllFurns(WallSegement ws, boolean bAddAccessibility)
		{
			boolean bIntersects = false;

			for(int x = 0 ; x < furnIds.size(); x++)
			{
				LineSegement ls = new LineSegement(ws.startP, ws.endP);

				// For Accessibility check
				List<Intersect> interList = new ArrayList<Intersect>();

				if(bAddAccessibility)
					interList = checkIntersectAccessibility(ls, furnIds.get(x));
				else
					interList = checkIntersect(ls, furnIds.get(x));

				for(Intersect inter : interList)
				{
					if(inter != null)
					{
						bIntersects = checkPointInBetween(inter.p, ls.startP, ls.endP, FURN_TOLERANCE);

						if(bIntersects)
							break;
					}
					//putMarkers(inter.p, 3);
				}
			

				if(bIntersects)
					break;
			

				if(bIntersects)
					break;
			}

			return bIntersects;
		}
		
		public boolean checkIntersectWitAllWalls(HomePieceOfFurniture hpf, boolean bAddAccessibility)
		{
			boolean bIntersects = false;

			for(int x = 0 ; x < wallIds.size(); x++)
			{				
				float[][] refFurnRect = wallRects.get(x);

				for(int f = 0; f < refFurnRect.length; f++)
				{
					Points startLine = new Points(refFurnRect[f][0], refFurnRect[f][1]);

					Points endLine = null;

					if(f == (refFurnRect.length - 1))
						endLine = new Points(refFurnRect[0][0], refFurnRect[0][1]);
					else
						endLine = new Points(refFurnRect[f+1][0], refFurnRect[f+1][1]);				

					LineSegement ls = new LineSegement(startLine, endLine);

					// For Accessibility check
					List<Intersect> interList = new ArrayList<Intersect>();

					if(bAddAccessibility)
						interList = checkIntersectAccessibility(ls, hpf.getName());
					else
						interList = checkIntersect(ls, hpf.getName());

					for(Intersect inter : interList)
					{
						if(inter != null)
						{
							bIntersects = checkPointInBetween(inter.p, ls.startP, ls.endP, FURN_TOLERANCE);

							if(bIntersects)
								break;
						}
						//putMarkers(inter.p, 6);
					}
				}

				if(bIntersects)
					break;
			}

			return bIntersects;
		}
		
		public boolean checkInsideRoom(Room inRoom, float[][] fRect, float tolr)
		{
			boolean bLiesInside = false;
			
			for(int f = 0; f < fRect.length; f++)
			{
				bLiesInside = inRoom.containsPoint(fRect[f][0], fRect[f][1], tolr);
				
				if(!bLiesInside)
					break;
			}
			
			//JOptionPane.showMessageDialog(null, bLiesInside);
			
			return bLiesInside;
		}
		
		public boolean checkInsideHome(List<WallSegement> inWSList, HomePieceOfFurniture refFurn, float tolr)
		{
			boolean bLiesInside = false;			

			float[][] fRect = refFurn.getPoints();
					
			for(int f = 0; f < fRect.length; f++)
			{
				bLiesInside = room.containsPoint(fRect[f][0], fRect[f][1], tolr);
				
				if(!bLiesInside)
					break;
			}
			
			return bLiesInside;
		}
		
		public List<Intersect> checkIntersectAccessibility(LineSegement r, String furnId)
		{
			List<Intersect> interList = new ArrayList<Intersect>();

			Intersect inter = null;
			int indx = -1;

			if((indx = furnIds.indexOf(furnId)) > -1)
			{ 				
				float[][] fRect = furnRectsAccess.get(indx);

				if(fRect.length == 2)
				{
					LineSegement l1 = new LineSegement((new Points(fRect[0][0], fRect[0][1])) , (new Points(fRect[1][0], fRect[1][1])));

					inter = getIntersectPoint(r, l1);				
					if(inter.dist < INFINITY)
						interList.add(inter);

					//debug += ("1. " + inter.p.x + "," + inter.p.y + " -> " + inter.dist + "\n");
				}
				else if(fRect.length == 4)
				{
					LineSegement l1 = new LineSegement((new Points(fRect[0][0], fRect[0][1])) , (new Points(fRect[1][0], fRect[1][1])));
					LineSegement l2 = new LineSegement((new Points(fRect[1][0], fRect[1][1])) , (new Points(fRect[2][0], fRect[2][1])));
					LineSegement l3 = new LineSegement((new Points(fRect[2][0], fRect[2][1])) , (new Points(fRect[3][0], fRect[3][1])));
					LineSegement l4 = new LineSegement((new Points(fRect[3][0], fRect[3][1])) , (new Points(fRect[0][0], fRect[0][1])));

					inter = getIntersectPoint(r, l1);				
					if(inter.dist < INFINITY)
						interList.add(inter);

					//debug += ("1. " + inter.p.x + "," + inter.p.y + " -> " + inter.dist + "\n");

					inter = getIntersectPoint(r, l2);				
					if(inter.dist < INFINITY)
						interList.add(inter);

					//debug += ("2. " + inter.p.x + "," + inter.p.y + " -> " + inter.dist + "\n");

					inter = getIntersectPoint(r, l3);
					if(inter.dist < INFINITY)
						interList.add(inter);

					//debug += ("3. " + inter.p.x + "," + inter.p.y + " -> " + inter.dist + "\n");

					inter = getIntersectPoint(r, l4);
					if(inter.dist < INFINITY)
						interList.add(inter);

					//debug += ("4. " + inter.p.x + "," + inter.p.y + " -> " + inter.dist + "\n");
				}
				//JOptionPane.showMessageDialog(null, debug);					
			}

			return interList;
		}

		public List<Intersect> checkIntersect(LineSegement r, String furnId)
		{
			List<Intersect> interList = new ArrayList<Intersect>();
			
			boolean bStop = false;

			Intersect inter = null;
			int indx = -1;

			if(!bStop && ((indx = furnIds.indexOf(furnId)) > -1))
			{ 				
				//float[][] fRect = furnRects.get(indx);
				float[][] fRect = furnRectsBloated.get(indx);

				if(fRect.length == 2)
				{
					LineSegement l1 = new LineSegement((new Points(fRect[0][0], fRect[0][1])) , (new Points(fRect[1][0], fRect[1][1])));

					inter = getIntersectPoint(r, l1);				
					if(inter.dist < INFINITY)
						interList.add(inter);

					//debug += ("1. " + inter.p.x + "," + inter.p.y + " -> " + inter.dist + "\n");
				}
				else if(fRect.length == 4)
				{
					LineSegement l1 = new LineSegement((new Points(fRect[0][0], fRect[0][1])) , (new Points(fRect[1][0], fRect[1][1])));
					LineSegement l2 = new LineSegement((new Points(fRect[1][0], fRect[1][1])) , (new Points(fRect[2][0], fRect[2][1])));
					LineSegement l3 = new LineSegement((new Points(fRect[2][0], fRect[2][1])) , (new Points(fRect[3][0], fRect[3][1])));
					LineSegement l4 = new LineSegement((new Points(fRect[3][0], fRect[3][1])) , (new Points(fRect[0][0], fRect[0][1])));

					inter = getIntersectPoint(r, l1);				
					if(inter.dist < INFINITY)
						interList.add(inter);

					//debug += ("1. " + inter.p.x + "," + inter.p.y + " -> " + inter.dist + "\n");

					inter = getIntersectPoint(r, l2);				
					if(inter.dist < INFINITY)
						interList.add(inter);

					//debug += ("2. " + inter.p.x + "," + inter.p.y + " -> " + inter.dist + "\n");

					inter = getIntersectPoint(r, l3);
					if(inter.dist < INFINITY)
						interList.add(inter);

					//debug += ("3. " + inter.p.x + "," + inter.p.y + " -> " + inter.dist + "\n");

					inter = getIntersectPoint(r, l4);
					if(inter.dist < INFINITY)
						interList.add(inter);

					//debug += ("4. " + inter.p.x + "," + inter.p.y + " -> " + inter.dist + "\n");
				}
				//JOptionPane.showMessageDialog(null, debug);					
			}

			return interList;
		}

		public Intersect getIntersectPointOfLines(LineSegement ref, LineSegement l)
		{
			Intersect inter = new Intersect((new Points()), 0.0f);

			float A = (ref.endP.y - ref.startP.y);											// (y2 - y1)
			float B = (ref.startP.x - ref.endP.x);											// (x1 - x2)		
			float C = ((ref.endP.y * ref.startP.x) - (ref.startP.y * ref.endP.x));			// (y2x1 - y1x2)

			float P = (l.endP.y - l.startP.y);												// (y2' - y1')
			float Q = (l.startP.x - l.endP.x);												// (x1' - x2')		
			float R = ((l.endP.y * l.startP.x) - (l.startP.y * l.endP.x));					// (y2'x1' - y1'x2')

			float yNum = (P*C - R*A);
			float yDen = (P*B - Q*A);

			float xNum = (Q*C - R*B);
			float xDen = (Q*A - P*B);
			
			JOptionPane.showMessageDialog(null, A+", "+B+", "+C+"; "+P+", "+Q+","+R);
					
			if(Math.abs(A) <= SLOPE_TOLERANCE)
			{
				if(Math.abs(P) <= SLOPE_TOLERANCE)
				{
					inter.p = new Points(l.startP.x, ref.startP.y);
				}
				else
				{
					float x = 0.0f - ((R + (Q * ref.startP.y)) / P);
					inter.p = new Points(x, ref.startP.y);
				}
			}
			else if(Math.abs(B) <= SLOPE_TOLERANCE)
			{
				if(Math.abs(Q) <= SLOPE_TOLERANCE)
				{
					inter.p = new Points(ref.startP.x, l.startP.y);
				}
				else
				{
					float y = 0.0f - ((R + (P * ref.startP.x)) / Q);
					inter.p = new Points(ref.startP.x, y);
				}
			}
			else if(Math.abs(P) <= SLOPE_TOLERANCE)
			{
				if(Math.abs(A) <= SLOPE_TOLERANCE)
				{
					inter.p = new Points(ref.startP.x, l.startP.y);
				}
				else
				{
					float x = 0.0f - ((C + (B * l.startP.y)) / A);
					inter.p = new Points(x, l.startP.y);
				}
			}
			else if(Math.abs(Q) <= SLOPE_TOLERANCE)
			{
				if(Math.abs(B) <= SLOPE_TOLERANCE)
				{
					inter.p = new Points(l.startP.x, ref.startP.y);
				}
				else
				{
					float y = 0.0f - ((C + (A * l.startP.x)) / B);
					inter.p = new Points(l.startP.x, y);
				}
			}
			else if((Math.abs(xDen) <= SLOPE_TOLERANCE) || (Math.abs(yDen) <= SLOPE_TOLERANCE))
			{
				inter.p = new Points(2*INFINITY, 2*INFINITY);
				inter.dist = INFINITY;
			}
			else
			{
				inter.p = new Points((xNum/xDen), (yNum/yDen));				
				inter.dist = calcDistance(inter.p, ref.startP);
			}

			return inter;			
		}

		public Intersect getIntersectPoint(LineSegement ref, LineSegement l)
		{
			Intersect inter = new Intersect((new Points()), 0.0f);

			float A = (ref.endP.y - ref.startP.y);											// (y2 - y1)
			float B = (ref.startP.x - ref.endP.x);											// (x1 - x2)		
			float C = ((ref.endP.y * ref.startP.x) - (ref.startP.y * ref.endP.x));			// (y2x1 - y1x2)

			float P = (l.endP.y - l.startP.y);												// (y2' - y1')
			float Q = (l.startP.x - l.endP.x);												// (x1' - x2')		
			float R = ((l.endP.y * l.startP.x) - (l.startP.y * l.endP.x));					// (y2'x1' - y1'x2')

			float yNum = (P*C - R*A);
			float yDen = (P*B - Q*A);

			float xNum = (Q*C - R*B);
			float xDen = (Q*A - P*B);

			if((xDen == 0.0f) || (yDen == 0.0f))
			{
				inter.p = new Points(2*INFINITY, 2*INFINITY);
				inter.dist = INFINITY;
			}
			else
			{
				inter.p = new Points((xNum/xDen), (yNum/yDen));				
				boolean bC1 = checkPointInBetween(inter.p, l.startP, l.endP, FURN_TOLERANCE);

				//JOptionPane.showMessageDialog(null, bC1 + " /  Intersection -> X : " + inter.p.x + ", Y : " + inter.p.y);

				if(bC1)
				{		
					inter.dist = calcDistance(inter.p, ref.startP);					
				}
				else
				{
					inter.p = new Points(INFINITY, INFINITY);
					inter.dist = INFINITY;
				}
			}

			return inter;			
		}
		
		public List<Points> getIntersectionCircleLine(Points center, float rad, Points startL, Points endL)
		{
			List<Points> interList = new ArrayList<Points>();
			
			try
			{	
				if(Math.abs(endL.x - startL.x) < tolerance)
				{
					float dist = (float) Math.abs(startL.x - center.x);
							
					if(dist <= rad)
					{
						float x01 = startL.x;
						float y01 = center.y - (float)Math.sqrt((rad*rad) - (dist*dist));
						
						Points inter1 = new Points(x01, y01);
						interList.add(inter1);
						//putMarkers(inter1, false);
						
						float x02 = startL.x;
						float y02 = center.y + (float)Math.sqrt((rad*rad) - (dist*dist));
						
						Points inter2 = new Points(x02, y02);
						interList.add(inter2);
						//putMarkers(inter2, false);
					}
					//else : Line does not intersect with this circle
				}
				else
				{
					// Equation of Line
					float m = ((endL.y - startL.y) / (endL.x - startL.x));
					float c = startL.y - (m*startL.x);
					
					// (m^2+1)x^2 + 2(mc−mq−p)x + (q^2−r^2+p^2−2cq+c^2) = 0			
					
					float A = (m*m) + 1;
					float B = 2*((m*c) - (m*center.y) - center.x);
					float C = (center.y*center.y) - (rad*rad) + (center.x*center.x) - 2*(c*center.y) + (c*c);
					
					float D = (B*B) - 4*A*C;
					
					if(D == 0)
					{
						float x1 = ((-B) + (float)Math.sqrt(D)) / (2*A);
						float y1 = (m*x1) + c;
						
						Points inter = new Points(x1, y1);
						interList.add(inter);	
						
						//putMarkers(inter, true);
					}
					else if (D > 0)
					{
						float x1 = ((-B) + (float)Math.sqrt(D)) / (2*A);
						float y1 = (m*x1) + c;
						
						Points inter1 = new Points(x1, y1);
						interList.add(inter1);
						
						//putMarkers(inter1, false);
						
						float x2 = ((-B) - (float)Math.sqrt(D)) / (2*A);
						float y2 = (m*x2) + c;
						
						Points inter2 = new Points(x2, y2);
						interList.add(inter2);
						
						//putMarkers(inter2, false);
					}
				}				
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(null," -xxxxx- EXCEPTION : " + e.getMessage()); 
				e.printStackTrace();
			}
			
			return interList;
		}
		
		public float calcDistance(Points p1, Points p2)
		{
			float d = (float) Math.sqrt(((p2.x - p1.x) * (p2.x - p1.x)) + ((p2.y - p1.y) * (p2.y - p1.y)));
			return d;
		}	

		public boolean isParallel(LineSegement ls1, LineSegement ls2, float tolr)
		{
			boolean isPara = false;

			float slope1 = 0.0f;
			float slope2 = 0.0f;

			if(Math.abs(ls1.endP.x - ls1.startP.x) <= tolr)
				slope1 = INFINITY;
			else
				slope1 = ((ls1.endP.y - ls1.startP.y) / (ls1.endP.x - ls1.startP.x));

			if(Math.abs(ls2.endP.x - ls2.startP.x) <= tolr)
				slope2 = INFINITY;
			else
				slope2 = ((ls2.endP.y - ls2.startP.y) / (ls2.endP.x - ls2.startP.x));

			//JOptionPane.showMessageDialog(null, Math.abs(ls1.endP.x - ls1.startP.x) + ", " + Math.abs(ls2.endP.x - ls2.startP.x));
			
			isPara = (Math.abs(slope1 - slope2) < SLOPE_TOLERANCE) ? true : false;

			return isPara;
		}

		public float calcDistancePointLine(Points p, LineSegement ls, float tolr)
		{
			float dist = 0.0f;

			if(Math.abs(ls.endP.x - ls.startP.x) < tolr)
			{
				dist = Math.abs(ls.endP.x - p.x);
			}
			else if(Math.abs(ls.endP.y - ls.startP.y) < tolr)
			{
				dist = Math.abs(ls.endP.y - p.y);
			}
			else
			{
				float slope = ((ls.endP.y - ls.startP.y) / (ls.endP.x - ls.startP.x));

				float A = slope;
				float B = -1.0f;
				float C = (ls.startP.y - (slope * ls.startP.x));

				dist = ( Math.abs((A*p.x) + (B*p.y) + C) / ((float)Math.sqrt((A*A) + (B*B))) );
			}

			return dist;
		}	

		public boolean checkPointInBetween(Points test, Points start, Points end, float tolPercent)
		{
			boolean bRet = false;

			float distST = calcDistance(start, test);
			float distTE = calcDistance(test, end);
			float distSE = calcDistance(start, end);

			float distSEAbs = (float)(Math.abs(distST + distTE - distSE));
					
			if(distSEAbs <= tolPercent)
				bRet = true;

			return bRet;			
		}

		public List<Points> sortPList(List<Points> interPList, Points ref)
		{
			List<Points> retPList = new ArrayList<Points>();
			TreeMap<Float, Points> pMap = new TreeMap<Float, Points>();

			for(Points p : interPList)
			{
				float dist = calcDistance(p, ref);
				pMap.put(dist, p);
			}

			Set<Float> keys = pMap.keySet();

			for(Float d : keys)
			{
				retPList.add(pMap.get(d));
			}

			return retPList;
		}

		public HomePieceOfFurniture getFurnItemFromCatalog(String furnName)
		{
			HomePieceOfFurniture matchFurn = null;
			List<FurnitureCategory> fCatg = getUserPreferences().getFurnitureCatalog().getCategories();		

			try 
			{
				for(int c = 0; c < fCatg.size(); c++ )
				{
					List<CatalogPieceOfFurniture> catPOF = fCatg.get(c).getFurniture();

					for(int p = 0; p < catPOF.size(); p++ )
					{
						if(furnName.equalsIgnoreCase(catPOF.get(p).getName()))
						{
							matchFurn = new HomePieceOfFurniture(catPOF.get(p));
							//JOptionPane.showMessageDialog(null, "Found " + furnName);
							break;
						}
					}	
				}				
			}
			catch(Exception e){e.printStackTrace();}

			return matchFurn;
		}

		// ======================= DEBUG FUNCTIONS ======================= //

		public void putMarkers(Points p, int indx)
		{
			HomePieceOfFurniture box = null;

			box = markBoxes[indx].clone();			
			box.setX(p.x);
			box.setY(p.y);
			home.addPieceOfFurniture(box);
		}

		public void putMarkerLine(LineSegement ls, int indx)
		{
			HomePieceOfFurniture box = null;

			box = markBoxes[indx].clone();			
			box.setX((ls.startP.x + ls.endP.x)/2.0f);
			box.setY((ls.startP.y + ls.endP.y)/2.0f);
			
			box.setWidth(calcDistance(ls.startP, ls.endP));
			box.setAngle(calcWallAngles(ls));
			
			box.setName(box.getName().replaceAll("box", "line"));
			
			home.addPieceOfFurniture(box);
		}
		
		public HomePieceOfFurniture[] getMarkerBoxes()
		{
			HomePieceOfFurniture[] markBoxes = new HomePieceOfFurniture[MARKBOX_COUNT];
			int count = 0;

			List<FurnitureCategory> fCatg = getUserPreferences().getFurnitureCatalog().getCategories();

			for(int c = 0; c < fCatg.size(); c++ )
			{
				if(count >= MARKBOX_COUNT)
					break;

				List<CatalogPieceOfFurniture> catPOF = fCatg.get(c).getFurniture();

				for(int p = 0; p < catPOF.size(); p++ )
				{
					if(catPOF.get(p).getName().equals("boxred"))
					{
						markBoxes[0] = new HomePieceOfFurniture(catPOF.get(p));
						markBoxName.add("boxred");
						count++;
					}
					else if(catPOF.get(p).getName().equals("boxgreen"))
					{
						markBoxes[1] = new HomePieceOfFurniture(catPOF.get(p));
						markBoxName.add("boxgreen");
						count++;
					}
					else if(catPOF.get(p).getName().equals("boxblue"))
					{
						markBoxes[2] = new HomePieceOfFurniture(catPOF.get(p));
						markBoxName.add("boxblue");
						count++;
					}
					else if(catPOF.get(p).getName().equals("boxyellow"))
					{
						markBoxes[3] = new HomePieceOfFurniture(catPOF.get(p));
						markBoxName.add("boxyellow");
						count++;
					}
					else if(catPOF.get(p).getName().equals("boxteal"))
					{
						markBoxes[4] = new HomePieceOfFurniture(catPOF.get(p));
						markBoxName.add("boxteal");
						count++;
					}
					else if(catPOF.get(p).getName().equals("boxblack"))
					{
						markBoxes[5] = new HomePieceOfFurniture(catPOF.get(p));
						markBoxName.add("boxblack");
						count++;
					}
					else if(catPOF.get(p).getName().equals("boxpurp"))
					{
						markBoxes[6] = new HomePieceOfFurniture(catPOF.get(p));
						markBoxName.add("boxpurp");
						count++;
					}
					else if(catPOF.get(p).getName().equals("boxgray"))
					{
						markBoxes[7] = new HomePieceOfFurniture(catPOF.get(p));
						markBoxName.add("boxgray");
						count++;
					}

					if(count >= MARKBOX_COUNT)
						break;
				}	
			}

			return markBoxes;
		}
	}



	@Override
	public PluginAction[] getActions() 
	{
		return new PluginAction [] {new RoomTestAction()}; 
	}
}
