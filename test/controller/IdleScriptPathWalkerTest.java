package controller;

import models.entities.MapPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static models.entities.MapPoint.BankPoint.*;
import static models.entities.MapPoint.MiningCampPoint.EAST_OF_THE_BATTLEFIELD;
import static models.entities.MapPoint.MiningCampPoint.LUMBRIDGE_SWAMP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class IdleScriptPathWalkerTest {
    private Controller controller;
    private IdleScriptPathWalker pathWalker;

    @BeforeEach
    void setUp() {
        controller = mock(Controller.class);
        pathWalker = new IdleScriptPathWalker(controller);
        pathWalker.init();
    }

    @Nested
    class CalcPathTest {

        @Test
        void fromLumbySwampToDraynorBank() {
            Path path = pathWalker.calcPath(LUMBRIDGE_SWAMP.getMapPoint(), DRAYNOR.getMapPoint());
            assertEquals(121, path.n.length);
        }

        @Test
        void fromLumbySwampToFaladorEastBank() {
            Path path = pathWalker.calcPath(LUMBRIDGE_SWAMP.getMapPoint(), FALADOR_EAST.getMapPoint());
            assertEquals(243, path.n.length);
        }

        @Test
        void fromVarrockEastBankToFaladorEastBank() {
            Path path = pathWalker.calcPath(VARROCK_EAST.getMapPoint(), FALADOR_EAST.getMapPoint());
            assertEquals(257, path.n.length);
        }

        @Test
        void fromVarrockEastBankToArdougneEastBank() {
            Path path = pathWalker.calcPath(VARROCK_EAST.getMapPoint(), ARDOUGNE_EAST.getMapPoint());
            assertEquals(592, path.n.length);
        }

        @Test
        void fromLumbySwampToEastOfPortKhazard() {
            Path path = pathWalker.calcPath(LUMBRIDGE_SWAMP.getMapPoint(), EAST_OF_THE_BATTLEFIELD.getMapPoint());
            assertEquals(871, path.n.length);
        }
    }

    @Nested
    class WalkToTest {
        private int currentX;
        private int currentY;

        @BeforeEach
        void setUp() {
            controller = mock(Controller.class);
            when(controller.currentX()).thenAnswer(i -> currentX);
            when(controller.currentY()).thenAnswer(i -> currentY);
            when(controller.isReachable(anyInt(), anyInt(), anyBoolean())).thenReturn(true);

            doAnswer(invocation -> {
                int arg1 = invocation.getArgument(0);
                int arg2 = invocation.getArgument(1);
                currentX = arg1;
                currentY = arg2;
                return null;
            })
                    .when(controller)
                    .walkTo(Mockito.anyInt(), Mockito.anyInt());

            pathWalker = new IdleScriptPathWalker(controller);
            pathWalker.init();
        }

        @Test
        void fromLumbySwampToDraynorBank() {
            MapPoint start = LUMBRIDGE_SWAMP.getMapPoint();
            currentX = start.getX();
            currentY = start.getY();

            pathWalker.setPath(pathWalker.calcPath(start, DRAYNOR.getMapPoint()));
            pathWalker.walkPath();

            int invocationCount = 0;
            while (pathWalker.walkPath()) {
                invocationCount++;
            }

            assertTrue(invocationCount > 700000);
        }
    }

}
