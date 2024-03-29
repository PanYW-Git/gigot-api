package top.panyuwen.gigotapibackend.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.panyuwen.gigotapibackend.annotation.AuthCheck;
import top.panyuwen.gigotapibackend.common.BaseResponse;
import top.panyuwen.gigotapibackend.common.ResultUtils;
import top.panyuwen.gigotapibackend.constant.UserConstant;
import top.panyuwen.gigotapibackend.service.*;
import top.panyuwen.gigotapicommon.model.entity.*;
import top.panyuwen.gigotapicommon.model.enums.ProductOrderStatusEnum;
import top.panyuwen.gigotapicommon.model.vo.analysis.*;

import javax.annotation.Resource;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static top.panyuwen.gigotapibackend.constant.RedisConstants.LOGIN_TOKEN_KEY;
import static top.panyuwen.gigotapibackend.constant.RedisConstants.SYSTEM_PV_KEY;

/**
 * 运行分析控制器
 *
 * @author PYW
 * @from www.panyuwen.top
 */
@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalyseController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private InterfaceLogService interfaceLogService;

    @Resource
    private ProductOrderService productOrderService;

    @Resource
    private ProductOrderTotalDayService productOrderTotalDayService;

    @Resource
    private ProductOrderPayoutRankService productOrderPayoutRankService;

    @Resource
    private ProductOrderPeriodIntroducerowService productOrderPeriodIntroducerowService;

    @Resource
    private InterfaceLogWeekCountService interfaceLogWeekCountService;

    @Resource
    private InterfaceInfoProportionService interfaceInfoProportionService;

    @GetMapping("/interface/introduceRow")
    public BaseResponse<IntroduceRowVO> getInterfaceIntroduceRow() {

        IntroduceRowVO introduceRowVO = new IntroduceRowVO();

        // 获取接口调用次数
        Long interfaceInfoCount = interfaceInfoService.getInterfaceInfoTotalInvokesCount();
        // 获取最近接口调用平均时间
        Integer cost = interfaceLogService.getInterfaceInfoAverageCost();
        introduceRowVO.setInterfaceInfoCount(interfaceInfoCount);
        introduceRowVO.setCost(cost);
        return ResultUtils.success(introduceRowVO);
    }

    @GetMapping("/introduceRow")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<IntroduceRowVO> getIntroduceRow() {
        Long size = stringRedisTemplate.opsForHyperLogLog().size(SYSTEM_PV_KEY);
        // 获取PV
        IntroduceRowVO introduceRowVO = new IntroduceRowVO();
        introduceRowVO.setPv(size);

        // 用户数
        Set<String> keys = stringRedisTemplate.keys( LOGIN_TOKEN_KEY + "*");
        if (ObjectUtil.isEmpty(keys)) {
            introduceRowVO.setOnLineUserCount(0L);
        }
        introduceRowVO.setOnLineUserCount(Long.valueOf(keys.size()));

        // 获取接口调用次数
        Long interfaceInfoCount = interfaceInfoService.getInterfaceInfoTotalInvokesCount();
        // 获取最近接口调用平均时间
        Integer cost = interfaceLogService.getInterfaceInfoAverageCost();
        introduceRowVO.setInterfaceInfoCount(interfaceInfoCount);
        introduceRowVO.setCost(cost);

        // 获取支付笔数
        Long successCount = productOrderService.getCountByStatus(ProductOrderStatusEnum.SUCCESS);
        introduceRowVO.setSuccessPayCount(successCount);

        // 未支付订单数
        Long noPayCount = productOrderService.getCountByStatus(ProductOrderStatusEnum.NOTPAY);
        introduceRowVO.setNoPayCount(noPayCount);

        // 总收入金额
        Long sucessTotalAmount = productOrderService.getSucessTotalAmount();
        introduceRowVO.setSucessTotalAmount(sucessTotalAmount);

        // 获取周期介绍
        ProductOrderPeriodIntroducerow productOrderPeriodIntroducerow = productOrderPeriodIntroducerowService.list().get(0);
        Long dayTotal = productOrderPeriodIntroducerow.getDayTotal();
        String dayOverDay = productOrderPeriodIntroducerow.getDayOverDay();
        String weekOverWeek = productOrderPeriodIntroducerow.getWeekOverWeek();
        introduceRowVO.setDayTotal(dayTotal);
        introduceRowVO.setDayOverDay(dayOverDay);
        introduceRowVO.setWeekOverWeek(weekOverWeek);

        return ResultUtils.success(introduceRowVO);
    }

    @GetMapping("/salesCard")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<SalesCardVO> getSalesCard(){
        List<ProductOrderTotalDay> productOrderTotalDayList = productOrderTotalDayService.list();
        List<ProductOrderPayoutRank> productOrderPayoutRankList = productOrderPayoutRankService.list();
        SalesCardVO salesCardVO = new SalesCardVO();
        salesCardVO.setTotalDay(productOrderTotalDayList);
        salesCardVO.setPayoutRank(productOrderPayoutRankList);
        return ResultUtils.success(salesCardVO);
    }

    @GetMapping("/topInterfaceInfo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<TopInterfaceInfoVO> getTopInterfaceInfo() {
        TopInterfaceInfoVO topInterfaceInfoVO = new TopInterfaceInfoVO();
        List<InterfaceLogWeekCount> interfaceLogWeekCountList = interfaceLogWeekCountService.list();
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list();
        List<InterfaceInfoTotalCountVO> interfaceInfoTotalCountVOList = interfaceInfoList.stream().map(
                interfaceInfo -> BeanUtil.copyProperties(interfaceInfo, InterfaceInfoTotalCountVO.class)
        ).collect(Collectors.toList());
        topInterfaceInfoVO.setInterfaceInfoTotalCount(interfaceInfoTotalCountVOList);
        topInterfaceInfoVO.setInterfaceLogWeekCounts(interfaceLogWeekCountList);
        topInterfaceInfoVO.setMostPopular(interfaceInfoList.get(0).getName());
        return ResultUtils.success(topInterfaceInfoVO);
    }

    @GetMapping("/interfaceinfoProportion")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<ProportionSalesVO> getInterfaceinfoProportion() {
        ProportionSalesVO proportionSalesVO = new ProportionSalesVO();
        List<InterfaceInfoProportion> interfaceInfoProportionList = interfaceInfoProportionService.list();
        proportionSalesVO.setInterfaceInfoProportionList(interfaceInfoProportionList);
        return ResultUtils.success(proportionSalesVO);
    }

}
